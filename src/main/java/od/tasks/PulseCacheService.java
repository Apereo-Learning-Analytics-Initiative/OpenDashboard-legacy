package od.tasks;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lti.LaunchRequest;
import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.PulseClassDetail;
import od.framework.model.PulseDateEventCount;
import od.framework.model.PulseDetail;
import od.framework.model.PulseStudentDetail;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.enrollment.EnrollmentProvider;
import od.providers.events.EventProvider;
import od.providers.lineitem.LineItemProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;
import od.repository.mongo.PulseCacheRepository;
import od.utils.PulseUtility;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.ModelOutput;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.emory.mathcs.backport.java.util.Arrays;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

@Service
public class PulseCacheService {
  
  private static final Logger log = LoggerFactory.getLogger(PulseCacheService.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;  
  @Autowired private PulseCacheRepository pulseCacheRepository;
  
  final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
  final static long MILLIS_PER_HOUR = 60 * 60 * 1000L;

  //@Async
  public CompletableFuture<String> pulseCache(final String tenantId, final String userId, final String classSourcedId, Set<Enrollment> enrollments) throws ProviderDataConfigurationException, ProviderException {
    
	  long startTime = System.currentTimeMillis();  
	  
    List<PulseDetail> pulseResults = pulseCacheRepository.findByUserIdAndTenantIdAndUserRoleAndClassSourcedId(userId, tenantId, "NONSTUDENT", classSourcedId);
    
    //if there is more then one cached pulseResult, we are in a weird state
    if(pulseResults!=null && pulseResults.size()==1) {      
      boolean moreThanDay = Math.abs((new Date()).getTime() - pulseResults.get(0).getLastUpdated().getTime()) > MILLIS_PER_DAY;
      if (!moreThanDay) {
    	  log.info("Recently Updated UserID: " + userId + " CourseId: " + classSourcedId + " was recently updated on " + pulseResults.get(0).getLastUpdated().getTime() + ", bypassing update");  
        return CompletableFuture.completedFuture("COMPLETED");
      }
      else {
    	  log.info("UserID: " + userId + " CourseId: " + classSourcedId + " caching commenced");
      }
    }
    
    
    
    boolean hasRiskScore = false;
    
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
    EventProvider eventProvider = providerService.getEventProvider(tenant);       
    UserProvider userProvider = providerService.getUserProvider(tenant);
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);  
    
    LineItemProvider lineItemProvider = null;
    try {
    	lineItemProvider = providerService.getLineItemProvider(tenant);
    }
    catch(ProviderDataConfigurationException e) {
    	log.warn("Line Item Provider not configured");
    }
    
    
    
    
    /**
     * Bug out if the class end date is after todays date
     */
    Class klassCheck = courseProvider.getClass(tenant, classSourcedId);
    String endDate = klassCheck.getMetadata().get(Vocabulary.CLASS_END_DATE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date classDate;
	try {
		classDate = sdf.parse(klassCheck.getMetadata().get(Vocabulary.CLASS_END_DATE));
	} catch (ParseException e2) {
		log.error("Error parsing start date, not caching");
    	return CompletableFuture.completedFuture("COMPLETED");
	}
    if(classDate.before(new Date())) {
    	log.info("Bipassing old class: " + classSourcedId);
    	return CompletableFuture.completedFuture("COMPLETED");
    } 
    
    
    
    Map<String,Map<String,Object>> modelOutputMap = null;
    try {
      ModelOutputProvider modelOutputProvider = providerService.getModelOutputProvider(tenant);
      ProviderData modelOutputProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.MODELOUTPUT);
      
      if (modelOutputProviderData != null) {
        Page<ModelOutput> page
          = modelOutputProvider.getModelOutputForContext(modelOutputProviderData, tenantId, classSourcedId, null);
        
        List<ModelOutput> output = page.getContent();
        
        if (output != null) {
          modelOutputMap 
            = output.stream().collect(Collectors.toMap(ModelOutput::getUserSourcedId, ModelOutput::getOutput));
          if (modelOutputMap != null) {
            hasRiskScore = true;
          }
        }        
      }
    }
    catch (Exception e) {
      log.warn(e.getMessage());
    }
    
    ProviderData lineitemProviderData = null;
    try {
      lineitemProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.LINEITEM);
    } 
    catch (Exception e) {
      log.warn(e.getMessage());
    }
    
    ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
    
    Set<Enrollment> t = new HashSet<>();
    for(Enrollment enrollment:enrollments) {
      if(!enrollment.getKlass().getSourcedId().equals(classSourcedId)) {
        t.add(enrollment);
      }
    }
    for(Enrollment enrollment:t) {
        enrollments.remove(enrollment);        
    }

    //at this point, we only have a single enrollment for this cached object
    
    
    PulseDetail pulseDetail = null;
    
    if (enrollments != null && !enrollments.isEmpty()) {
      
      if (StringUtils.isNotBlank(classSourcedId)) {
        Enrollment foundEnrollment
          = enrollments.stream().filter(e -> e.getKlass().getSourcedId().equals(classSourcedId))
              .findFirst().orElse(null);  
      
        Class klass = courseProvider.getClass(tenant, classSourcedId);  
        
        if (foundEnrollment == null) {
          foundEnrollment
            = new Enrollment.Builder()
                .withKlass(klass)
                .build();
        } else {
          //we have to do this because the Klass that we get from the EnrollmentProvider
          //is only the source id.
            foundEnrollment
              = new Enrollment.Builder()
                  .withKlass(klass)
                  .withMetadata(foundEnrollment.getMetadata())
                  .withRole(foundEnrollment.getRole())
                  .withSourcedId(foundEnrollment.getSourcedId())
                  .withStatus(foundEnrollment.getStatus())
                  .withUser(foundEnrollment.getUser())
                  .withPrimary(foundEnrollment.isPrimary())
                  .build();
        }
        
        enrollments = Collections.singleton(foundEnrollment);
      }
      else {
        Set<Enrollment> tempEnrollments = new HashSet<>();
        for (Enrollment e : enrollments) {
          Enrollment copyOfEnrollment
            = new Enrollment.Builder()
              .withKlass(courseProvider.getClass(tenant, e.getKlass().getSourcedId()))
              .withMetadata(e.getMetadata())
              .withPrimary(e.isPrimary())
              .withRole(e.getRole())
              .withSourcedId(e.getSourcedId())
              .withStatus(e.getStatus())
              .withUser(e.getUser())
              .build();
          tempEnrollments.add(copyOfEnrollment);
        }
        enrollments = tempEnrollments;
      }
      
      List<PulseClassDetail> pulseClassDetails = new ArrayList<>();
      
      Set<LocalDate> allClassStartDates = new HashSet<>();
      Set<LocalDate> allClassEndDates = new HashSet<>();
      Set<Integer> allClassStudentEventCounts = new HashSet<>();
      Set<Long> allStudentEventCounts = new HashSet<>();

      for (Enrollment enrollment: enrollments) {
        
        unicon.matthews.oneroster.Class klass
          = enrollment.getKlass();
        
        LocalDate classStartDate = null;
        LocalDate classEndDate = null;
        
        Map<String, String> classMetadata = klass.getMetadata();
        if (classMetadata != null) {
          String classStartDateString = classMetadata.get(Vocabulary.CLASS_START_DATE);
          
          if (StringUtils.isNotBlank(classStartDateString)) {
            classStartDate = LocalDate.parse(classStartDateString);
            if (classStartDate != null){
              allClassStartDates.add(classStartDate);
            }
          }
          
          String classEndDateString = classMetadata.get(Vocabulary.CLASS_END_DATE);
          
          if (StringUtils.isNotBlank(classEndDateString)) {
            classEndDate = LocalDate.parse(classEndDateString);
            if (classEndDate != null){
              allClassEndDates.add(classEndDate);
            }
          }
        }
                
        ClassEventStatistics classEventStatistics = null;
        try {
          boolean studentsOnly = true;          
          classEventStatistics = eventProvider.getStatisticsForClass(tenantId, klass.getSourcedId(), studentsOnly);
        } 
        catch (Exception e1) {
          log.warn(e1.getMessage(),e1);
        }
        
        Set<Enrollment> classEnrollment = enrollmentProvider.getEnrollmentsForClass(rosterProviderData, klass.getSourcedId(), true);        
        
        if (classEnrollment != null && !classEnrollment.isEmpty()) {
          
          // temp workaround
          ProviderData userProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.USER);
          Set<Enrollment> populatedEnrollments = new HashSet<>();
          for (Enrollment e : classEnrollment) {
            User tempUser = null;
            try {
              tempUser = userProvider.getUserBySourcedId(userProviderData, e.getUser().getSourcedId());
            }
            catch (Exception ex) {
              log.info("Could not find user record for user {}", e.getUser().getSourcedId());
            }
            
            if (tempUser == null) {
              continue;
            }
            
            Enrollment populatedEnrollment
              = new Enrollment.Builder()
                  .withKlass(e.getKlass())
                  .withMetadata(e.getMetadata())
                  .withPrimary(e.isPrimary())
                  .withRole(e.getRole())
                  .withSourcedId(e.getSourcedId())
                  .withStatus(e.getStatus())
                  .withUser(tempUser)
                  .build();
            
            populatedEnrollments.add(populatedEnrollment);
          }
          classEnrollment = populatedEnrollments;
          
          classEnrollment = 
              classEnrollment.stream().filter(ce -> ce.getRole().equals(Role.student)).collect(Collectors.toSet());
        }
        
        List<PulseDateEventCount> classPulseDateEventCounts = null;
        LocalDate firstClassEventDate = null;
        LocalDate lastClassEventDate = null;
        if (classEventStatistics != null) {

          Map<String,Long> eventCountGroupedByDate = classEventStatistics.getEventCountGroupedByDate();
          if (eventCountGroupedByDate != null && !eventCountGroupedByDate.isEmpty()) {
            classPulseDateEventCounts = new ArrayList<>();
            for (String key : eventCountGroupedByDate.keySet()) {
              PulseDateEventCount pulseDateEventCount
                = new PulseDateEventCount.Builder()
                  .withDate(LocalDate.parse(key))
                  .withEventCount(eventCountGroupedByDate.get(key) != null ? eventCountGroupedByDate.get(key).intValue() : null)
                  .build();
              classPulseDateEventCounts.add(pulseDateEventCount);
            }
            
            
            //class event statistics could calculate this without having to go through the stream
            firstClassEventDate = classPulseDateEventCounts.stream().map(PulseDateEventCount::getDate).min(LocalDate::compareTo).get();
            lastClassEventDate = classPulseDateEventCounts.stream().map(PulseDateEventCount::getDate).max(LocalDate::compareTo).get();
          }
        }
        
        Set<LineItem> classLineItems = null;
        LocalDate lastAssignmentDueDate = null;
        boolean hasAssignments = false;
        if (lineitemProviderData != null && lineItemProvider != null) {
          try {
            classLineItems = lineItemProvider.getLineItemsForClass(lineitemProviderData, klass.getSourcedId());
          }
          catch (Exception e) {
            log.info(e.getLocalizedMessage());
          }
          if (classLineItems != null && !classLineItems.isEmpty()) {
            hasAssignments = true;

            Collection<LocalDate> assignmentDueDates = new ArrayList<>();
            for (LineItem cli : classLineItems) {
              LocalDateTime dueDate = cli.getDueDate();
              if (dueDate != null) {
                assignmentDueDates.add(dueDate.toLocalDate());
              }
            }

            lastAssignmentDueDate = assignmentDueDates.stream().max(LocalDate::compareTo).get();
          }
        }
        
        if (classStartDate == null) {
          if (firstClassEventDate != null) {
            classStartDate = firstClassEventDate;
          }
          else {
            classStartDate = LocalDate.now().minus(1,ChronoUnit.MONTHS);
          }
          allClassStartDates.add(classStartDate);
        }
        
        if (classEndDate == null) {
          if (lastAssignmentDueDate != null) {
            classEndDate = lastAssignmentDueDate;
          }
          else if (lastClassEventDate != null) {
            classEndDate = lastClassEventDate;
          }
          else {
            classEndDate = LocalDate.now().plus(1, ChronoUnit.MONTHS);
          }
          allClassEndDates.add(classEndDate);
        }
        
        List<PulseStudentDetail> pulseStudentDetails = null;
        if (classEnrollment != null && !classEnrollment.isEmpty()) {
          
          pulseStudentDetails = new ArrayList<>();
          
          for (Enrollment studentEnrollment: classEnrollment) {
            
            List<PulseDateEventCount> studentPulseDateEventCounts = null;
            List<LocalDate> allStudentEventDates = new ArrayList<>();
            
            if (classEventStatistics != null) {
              Map<String,Map<String,Long>> studentEventCountGroupedByDate = classEventStatistics.getEventCountGroupedByDateAndStudent();
              if (studentEventCountGroupedByDate != null && !studentEventCountGroupedByDate.isEmpty()) {
                Map<String, Long> studentEventCountByDate = studentEventCountGroupedByDate.get(studentEnrollment.getUser().getSourcedId());
                
                if (studentEventCountByDate != null && !studentEventCountByDate.isEmpty()) {
                  
                  studentPulseDateEventCounts = new ArrayList<>();
                  for (String key : studentEventCountByDate.keySet()) {
                    LocalDate studentEventDate = LocalDate.parse(key);
                    Long eventCount = studentEventCountByDate.get(key);
                    allStudentEventDates.add(studentEventDate);
                    allStudentEventCounts.add(eventCount);
                    PulseDateEventCount pulseDateEventCount
                      = new PulseDateEventCount.Builder()
                        .withDate(studentEventDate)
                        .withEventCount(eventCount != null ? eventCount.intValue() : null)
                        .build();
                    studentPulseDateEventCounts.add(pulseDateEventCount);
                  }
                }
              }
            }
            
            Long activity = 0l;
            if (studentPulseDateEventCounts != null) {
              
              //class event statistics could calculate this without having to go through the stream (on the LRW side)
              activity = studentPulseDateEventCounts.stream().mapToLong(PulseDateEventCount::getEventCount).sum();
            }
            else {
              studentPulseDateEventCounts = new ArrayList<>();
            }
            
            Long daysSinceLogin = 0l;
            if (!allStudentEventDates.isEmpty()) {
              daysSinceLogin = java.time.temporal.ChronoUnit.DAYS.between(allStudentEventDates.stream().max(LocalDate::compareTo).get(), LocalDate.now());
            }

            String modifiedStudentId = PulseUtility.escapeForPulse(studentEnrollment.getUser().getSourcedId());
            
            //Handle risk Score... 
            String riskScore = "NA";
            if( 
                modelOutputMap != null &&
                studentEnrollment != null &&
                studentEnrollment.getUser() != null &&
                studentEnrollment.getUser().getSourcedId() != null &&
                modelOutputMap.get(studentEnrollment.getUser().getSourcedId())!= null
              ) {
              riskScore = modelOutputMap.get(studentEnrollment.getUser().getSourcedId()).get("RISK_SCORE").toString();
            }
            
            PulseStudentDetail pulseStudentDetail
              = new PulseStudentDetail.Builder()
                .withId(modifiedStudentId)
                .withLabel(studentEnrollment.getUser().getFamilyName() +", "+studentEnrollment.getUser().getGivenName())                  
                .withFirstName(studentEnrollment.getUser().getGivenName())
                .withLastName(studentEnrollment.getUser().getFamilyName())
                .withEmail(studentEnrollment.getUser().getEmail())
                .withRisk(hasRiskScore ? riskScore : null)
                .withGrade(null)
                .withMissingSubmission(false)
                .withActivity(activity) 
                .withEvents(studentPulseDateEventCounts)
                .withDaysSinceLogin(daysSinceLogin)
                
                .build();
            
            pulseStudentDetails.add(pulseStudentDetail);

          }
        }
                
        Integer studentEventMax = 0;
        if (!allStudentEventCounts.isEmpty()) {
          
          //class event statistics could calculate this without having to go through the entire stack
          studentEventMax = Collections.max(allStudentEventCounts).intValue();
        }
        
        if (classPulseDateEventCounts == null) {
          classPulseDateEventCounts = new ArrayList<>();
        }
        else {
          int classEventMax = classPulseDateEventCounts.stream().mapToInt(PulseDateEventCount::getEventCount).max().getAsInt();
          allClassStudentEventCounts.add(classEventMax);
        }
                               
        String modifiedClassId = PulseUtility.escapeForPulse(klass.getSourcedId());
        PulseClassDetail pulseClassDetail
          = new PulseClassDetail.Builder()
        
            .withId(modifiedClassId)
            .withLabel(klass.getTitle())            
            
            .withHasAssignments(hasAssignments)
            .withHasGrade(false)
            .withHasMissingSubmissions(false)
            .withHasRisk(hasRiskScore)
            
            .withStartdate(classStartDate)
            .withEnddate(classEndDate)
            
            .withStudentEventMax(studentEventMax)
            .withStudentEventTotalMax(
                pulseStudentDetails != null ? 
                    pulseStudentDetails.stream().mapToLong(PulseStudentDetail::getActivity).max().getAsLong() :
                      0l)
            .withAssignments(classLineItems != null ? new ArrayList<>(classLineItems) : new ArrayList<>())
            .withEvents(classPulseDateEventCounts)
            .withStudents(pulseStudentDetails)
            .withMeanStudentEvents(classEventStatistics.getMeanStudentEvents())
            .withMedianStudentEvents(getMedianStudentEvents(pulseStudentDetails))
            .withEventTypeAverages(classEventStatistics.getEventTypeAverages())
            .withEventTypeTotals(classEventStatistics.getEventTypeTotals())
            .withStudentsWithEvents(classEventStatistics.getStudentsWithEvents())
            .withMeanPassPercent(getAverageRiskScore(pulseStudentDetails))
            .withMedianPassPercent(getMedianRiskScore(pulseStudentDetails))
            .withTotalNumberOfEvents(classEventStatistics.getTotalEvents())
            .build();
        
        pulseClassDetails.add(pulseClassDetail);
      }      
      
      Integer classEventMax = 0;
      if (allClassStudentEventCounts != null && !allClassStudentEventCounts.isEmpty()) {
        classEventMax = allClassStudentEventCounts.stream().max(Integer::compareTo).get();
      }
      
      pulseDetail
        = new PulseDetail.Builder()
          .withEndDate(allClassEndDates.stream().max(LocalDate::compareTo).get())
          .withStartDate(allClassStartDates.stream().min(LocalDate::compareTo).get())
          .withClassEventMax(classEventMax)
          .withHasGrade(false)
          .withHasRisk(hasRiskScore)
          .withHasMissingSubmissions(false)
          .withHasLastLogin(false)
          .withHasEmail(true)
          .withPulseClassDetails(pulseClassDetails)
          .withUserId(userId)
          .withTenantId(tenantId)
          .withUserRole("NONSTUDENT")
          .withClassSourcedId(classSourcedId)
          .build();
    }    
    
    //delete them all if they exist
    pulseCacheRepository.deleteByUserIdAndTenantIdAndUserRoleAndClassSourcedId(userId, tenantId,"NONSTUDENT",classSourcedId);    
    pulseCacheRepository.save(pulseDetail);
    long endTime = System.currentTimeMillis();    
    log.info("UserID: " + userId + " CourseId: " + classSourcedId + " caching finished" + "That took " + (endTime - startTime) + " milliseconds");
    return CompletableFuture.completedFuture("COMPLETED");
  }
  
  
  
  
  
  
  private double getAverageRiskScore(List<PulseStudentDetail> pulseStudentDetails) {
    if (pulseStudentDetails == null) {
      return 0;
    }
    double cumulator = 0.0;
    for(PulseStudentDetail studentDetail : pulseStudentDetails) {
      if(!Double.isNaN(studentDetail.getRiskAsDouble()))
      {
        cumulator += studentDetail.getRiskAsDouble();
      }
    }
    
    DecimalFormat df = new DecimalFormat("###.###");
    double averageRiskScore = Double.valueOf(df.format(cumulator/pulseStudentDetails.size()));
    return averageRiskScore;
  }
  
  private double getMedianRiskScore(List<PulseStudentDetail> pulseStudentDetails) {
    if (pulseStudentDetails == null) {
      return 0;
    }
    
    List<Double> allRiskScores = new ArrayList<>();
    for(PulseStudentDetail studentDetail : pulseStudentDetails) {
      if(!Double.isNaN(studentDetail.getRiskAsDouble()))
      {
        allRiskScores.add(studentDetail.getRiskAsDouble());
      }
    }
    
    if (allRiskScores.size() <= 0) {
      return Double.NaN;
    }
    
    double[] riskArray = allRiskScores.stream().mapToDouble(Double::doubleValue).toArray();
    
    Arrays.sort(riskArray);
    double median;
    if (riskArray.length % 2 == 0)
        median = ((double)riskArray[riskArray.length/2] + (double)riskArray[riskArray.length/2 - 1])/2;
    else
        median = (double) riskArray[riskArray.length/2];
    
    return median;
}
  
  
  
  private Long getMedianStudentEvents(List<PulseStudentDetail> pulseStudentDetails) {
    
    if (pulseStudentDetails == null) {
      return 0l;
    }
    
    List<Long> allActivityCount = new ArrayList<>();
    for(PulseStudentDetail studentDetail : pulseStudentDetails) {
      if(!Double.isNaN(studentDetail.getActivity()))
      {
        allActivityCount.add(studentDetail.getActivity());
      }
    }
    
    if (allActivityCount.size() <= 0) {
      return 0l;
    }
    long[] studentEventsArray = allActivityCount.stream().mapToLong(l -> l).toArray();
    //long[] riskArray =  allActivityCount.stream().mapToLong(Long::toLong).toArray();
    
    Arrays.sort(studentEventsArray);
    double median;
    if (studentEventsArray.length % 2 == 0)
        median = ((long)studentEventsArray[studentEventsArray.length/2] + (long)studentEventsArray[studentEventsArray.length/2 - 1])/2;
    else
        median = (long) studentEventsArray[studentEventsArray.length/2];
    
    return (long) median;
  }
}
