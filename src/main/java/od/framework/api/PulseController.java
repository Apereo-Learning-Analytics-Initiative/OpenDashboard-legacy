package od.framework.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.Role;
import unicon.oneroster.Vocabulary;

@RestController
public class PulseController {
  
  private static final Logger log = LoggerFactory.getLogger(PulseController.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR"})
  @RequestMapping(value = "/api/tenants/{tenantId}/pulse/{userId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public PulseDetail pulse(Authentication authentication, @PathVariable("tenantId") final String tenantId,
      @PathVariable("userId") final String userId) throws ProviderDataConfigurationException, ProviderException {
    
    log.debug("tenantId: {}", tenantId);
    log.debug("userId: {}", userId);
    log.debug("Authentication: {}", authentication);
    
    String tempClassSourcedId = null;
    
    if (authentication != null && authentication instanceof OpenDashboardAuthenticationToken) {
      OpenDashboardAuthenticationToken openDashboardAuthenticationToken
        = (OpenDashboardAuthenticationToken)authentication;
      
      LaunchRequest launchRequest = openDashboardAuthenticationToken.getLaunchRequest();
      if (launchRequest != null) {
        tempClassSourcedId = launchRequest.getContext_id();
      }
    }
    final String classSourcedId = tempClassSourcedId;
    
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
    EventProvider eventProvider = providerService.getEventProvider(tenant);
    LineItemProvider lineItemProvider = providerService.getLineItemProvider(tenant);
    UserProvider userProvider = providerService.getUserProvider(tenant);
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);
    
    ProviderData lineitemProviderData = null;
    try {
      lineitemProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.LINEITEM);
    } 
    catch (Exception e) {
      log.warn(e.getMessage());
    }
    
    ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
    
    Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForUser(rosterProviderData, userId, true);
    
    PulseDetail pulseDetail = null;
    
    if (enrollments != null && !enrollments.isEmpty()) {
      
      if (StringUtils.isNotBlank(classSourcedId)) {
        Enrollment foundEnrollment
          = enrollments.stream().filter(e -> e.getKlass().getSourcedId().equals(classSourcedId))
              .findFirst().orElse(null);
        
        if (foundEnrollment == null) {
                    
          Class klass = courseProvider.getClass(tenant, classSourcedId);
          
          foundEnrollment
            = new Enrollment.Builder()
                .withKlass(klass)
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
                
        ClassEventStatistics classEventStatistics = eventProvider.getStatisticsForClass(tenantId, klass.getSourcedId());
        Set<Enrollment> classEnrollment = enrollmentProvider.getEnrollmentsForClass(rosterProviderData, klass.getSourcedId(), true);        
        
        if (classEnrollment != null && !classEnrollment.isEmpty()) {
          
          // temp workaround
          ProviderData userProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.USER);
          Set<Enrollment> populatedEnrollments = new HashSet<>();
          for (Enrollment e : classEnrollment) {
            Enrollment populatedEnrollment
              = new Enrollment.Builder()
                  .withKlass(e.getKlass())
                  .withMetadata(e.getMetadata())
                  .withPrimary(e.isPrimary())
                  .withRole(e.getRole())
                  .withSourcedId(e.getSourcedId())
                  .withStatus(e.getStatus())
                  .withUser(userProvider.getUserBySourcedId(userProviderData, e.getUser().getSourcedId()))
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
            
            firstClassEventDate = classPulseDateEventCounts.stream().map(PulseDateEventCount::getDate).min(LocalDate::compareTo).get();
            lastClassEventDate = classPulseDateEventCounts.stream().map(PulseDateEventCount::getDate).max(LocalDate::compareTo).get();
          }
        }
        
        Set<LineItem> classLineItems = null;
        LocalDate lastAssignmentDueDate = null;
        boolean hasAssignments = false;
        if (lineitemProviderData != null) {
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
//            classLineItems
//              = classLineItems.stream().filter(li -> li.getStatus().equals(Status.active)).collect(Collectors.toSet());
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
              activity = studentPulseDateEventCounts.stream().mapToLong(PulseDateEventCount::getEventCount).sum();
            }
            else {
              studentPulseDateEventCounts = new ArrayList<>();
            }
            
            Long daysSinceLogin = 0l;
            if (!allStudentEventDates.isEmpty()) {
              daysSinceLogin = java.time.temporal.ChronoUnit.DAYS.between(allStudentEventDates.stream().max(LocalDate::compareTo).get(), LocalDate.now());
            }
            
            PulseStudentDetail pulseStudentDetail
              = new PulseStudentDetail.Builder()
            
                .withId(StringUtils.replace(studentEnrollment.getUser().getSourcedId(), ":", ""))
                .withLabel(studentEnrollment.getUser().getFamilyName() +", "+studentEnrollment.getUser().getGivenName())
                .withFirstName(studentEnrollment.getUser().getGivenName())
                .withLastName(studentEnrollment.getUser().getFamilyName())
                .withEmail(studentEnrollment.getUser().getEmail())
                
                .withRisk(null)
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
          studentEventMax = Collections.max(allStudentEventCounts).intValue();
        }
        
        if (classPulseDateEventCounts == null) {
          classPulseDateEventCounts = new ArrayList<>();
        }
        else {
          int classEventMax = classPulseDateEventCounts.stream().mapToInt(PulseDateEventCount::getEventCount).max().getAsInt();
          allClassStudentEventCounts.add(classEventMax);
        }
                
        PulseClassDetail pulseClassDetail
          = new PulseClassDetail.Builder()
        
            .withId(klass.getSourcedId())
            .withLabel(klass.getTitle())
            
            .withHasAssignments(hasAssignments)
            .withHasGrade(false)
            .withHasMissingSubmissions(false)
            .withHasRisk(false)
            
            .withStartdate(classStartDate)
            .withEnddate(classEndDate)
            
            .withStudentEventMax(studentEventMax)
            .withStudentEventTotalMax(pulseStudentDetails.stream().mapToLong(PulseStudentDetail::getActivity).max().getAsLong())
            .withAssignments(classLineItems != null ? new ArrayList<>(classLineItems) : new ArrayList())
            .withEvents(classPulseDateEventCounts)
            .withStudents(pulseStudentDetails)
            
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
          .withHasRisk(false)
          .withHasMissingSubmissions(false)
          .withHasLastLogin(false)
          .withHasEmail(false)
          .withPulseClassDetails(pulseClassDetails)
          .build();
    }

    return pulseDetail;
  }
  
}
