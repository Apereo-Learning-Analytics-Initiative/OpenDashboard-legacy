/**
 * 
 */
package od.providers.events;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import od.exception.MethodNotImplementedException;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ggilbert
 *
 */
@Component("events_demo")
public class DemoEventProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(DemoEventProvider.class);
  
  private static final String KEY = "events_demo";
  private static final String BASE = "OD_DEMO_EVENTS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  private Set<Event> demoClass1Events;
  private Set<Event> demoClass2Events;
  private Set<Event> demoClass3Events;
  
  private Map<String, Set<Event>> classEventsMap;
  private Map<String, ClassEventStatistics> classEventsStatsMap;
  
  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public ProviderConfiguration getProviderConfiguration() {
    // Not needed for demo provider
    return new ProviderConfiguration() {
      
      @Override
      public LinkedList<ProviderConfigurationOption> getOptions() {
        return new LinkedList<>();
      }
      
      @Override
      public ProviderConfigurationOption getByKey(String key) {
        return null;
      }
    };
  }
  
  private long getRandomTimeBetweenTwoDates (long beginTime, long endTime) {
    long diff = endTime - beginTime + 1;
    return beginTime + (long) (Math.random() * diff);
  }
  
  @PostConstruct
  public void init() {
    
    demoClass1Events = new HashSet<>();
    demoClass2Events = new HashSet<>();
    demoClass3Events = new HashSet<>();
    
    classEventsMap = new HashMap<>();
    classEventsMap.put("demo-class-1", demoClass1Events);
    classEventsMap.put("demo-class-2", demoClass2Events);
    classEventsMap.put("demo-class-3", demoClass3Events);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    String [] classSourcedIds = {"demo-class-1","demo-class-2","demo-class-3"};
    
    long demoClass1StartTime = Timestamp.valueOf("2016-08-30 00:00:00").getTime();
    long demoClass1EndTime = Timestamp.valueOf("2016-12-11 00:00:00").getTime();
    long demoClass2StartTime = Timestamp.valueOf("2016-09-01 00:00:00").getTime();
    long demoClass2EndTime = Timestamp.valueOf("2016-12-10 00:00:00").getTime();
    long demoClass3StartTime = Timestamp.valueOf("2016-09-08 00:00:00").getTime();
    long demoClass3EndTime = Timestamp.valueOf("2016-12-13 00:00:00").getTime();
    
    String [] verbs = {
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Bookmarked",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Commented",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Completed",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedIn",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedOut",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#NavigatedTo",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Recommended",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Searched",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Shared",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Submitted",
        "http://purl.imsglobal.org/vocab/caliper/v1/action#Viewed"
    };
    
    
    List<String> studentSourcedIds = new LinkedList<>();
    for (int s = 0; s < 60; s++) {
      String studentSourcedId = "demo-student-".concat(String.valueOf(s));
      studentSourcedIds.add(studentSourcedId);
    }
    
    for(int e = 0; e < 60000; e++) {
      EventImpl eventImpl = new EventImpl();
      eventImpl.setActor(studentSourcedIds.get(ThreadLocalRandom.current().nextInt(0, 60)));
      eventImpl.setContext(classSourcedIds[ThreadLocalRandom.current().nextInt(0, 3)]);
      eventImpl.setId(UUID.randomUUID().toString());
      eventImpl.setObject("http://edapp.com/object");
      eventImpl.setObjectType("SoftwareApplication");
      eventImpl.setSourceId(UUID.randomUUID().toString());
      
      String timestamp = null;
      
      if ("demo-class-1".equals(eventImpl.getContext())) {       
        demoClass1Events.add(eventImpl);
        
        long rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
        Calendar c = Calendar.getInstance();
        Date randomDate = new Date(rnd);
        c.setTime(randomDate);
        
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && e % 2 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && e % 3 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && e % 4 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }       
        
        timestamp = dateFormat.format(randomDate);
      }
      else if ("demo-class-2".equals(eventImpl.getContext())) {
        demoClass2Events.add(eventImpl);
        
        long rnd = getRandomTimeBetweenTwoDates(demoClass2StartTime, demoClass2EndTime);
        Calendar c = Calendar.getInstance();
        Date randomDate = new Date(rnd);
        c.setTime(randomDate);
        
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && e % 2 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && e % 3 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && e % 4 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }       
        
        timestamp = dateFormat.format(randomDate);
      }
      else {
        demoClass3Events.add(eventImpl);
        
        long rnd = getRandomTimeBetweenTwoDates(demoClass3StartTime, demoClass3EndTime);
        Calendar c = Calendar.getInstance();
        Date randomDate = new Date(rnd);
        c.setTime(randomDate);
        
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && e % 2 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && e % 3 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && e % 4 == 0) {
          rnd = getRandomTimeBetweenTwoDates(demoClass1StartTime, demoClass1EndTime);
          randomDate = new Date(rnd);
        }       
        
        timestamp = dateFormat.format(randomDate);
      }
      
      eventImpl.setTimestamp(timestamp);
      eventImpl.setVerb(verbs[ThreadLocalRandom.current().nextInt(0, 11)]);
    }
  }

  @Override
  public ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId) throws ProviderException {
    
    Set<Event> classEvents = classEventsMap.get(classSourcedId);
    
    Map<String, Long> studentsCounted = classEvents.stream()
        .collect(Collectors.groupingBy(event -> ((EventImpl)event).getActor(), Collectors.counting()));
    
    Map<String, List<Event>> eventsByStudent = classEvents.stream()
        .collect(Collectors.groupingBy(event -> ((EventImpl)event).getActor()));
    
    Map<String,Map<String, Long>> eventCountGroupedByDateAndStudent = null;
    
    if (eventsByStudent != null) {
      eventCountGroupedByDateAndStudent = new HashMap<>();
      for (String key : eventsByStudent.keySet()) {
        Map<String, Long> eventCountByDate = eventsByStudent.get(key).stream()
            .collect(Collectors.groupingBy(event -> StringUtils.substringBefore(((EventImpl)event).getTimestamp(), " "), Collectors.counting()));
        eventCountGroupedByDateAndStudent.put(key, eventCountByDate);
      }
    }

    Map<String, Long> eventCountByDate = classEvents.stream()
        .collect(Collectors.groupingBy(event -> StringUtils.substringBefore(((EventImpl)event).getTimestamp(), " "), Collectors.counting()));

    if (classEventsStatsMap == null) {
      classEventsStatsMap = new HashMap<>();
    }
    
    ClassEventStatistics classEventsStats = classEventsStatsMap.get(classSourcedId);
    
    if (classEventsStats == null) {
      classEventsStats 
        = new ClassEventStatistics.Builder()
          .withClassSourcedId(classSourcedId)
          .withTotalEvents(classEvents.size())
          .withTotalStudentEnrollments(studentsCounted.keySet().size())
          .withEventCountGroupedByDate(eventCountByDate)
          .withEventCountGroupedByDateAndStudent(eventCountGroupedByDateAndStudent)
          .build();
      
      classEventsStatsMap.put(classSourcedId, classEventsStats);
    }
    
    return classEventsStats;
  }

  /* (non-Javadoc)
   * @see od.providers.events.EventProvider#getEventsForUser(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
   */
  @Override
  public Page<Event> getEventsForUser(String tenantId, String userId, Pageable pageable) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see od.providers.events.EventProvider#getEventsForCourse(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
   */
  @Override
  public Page<Event> getEventsForCourse(String tenantId, String courseId, Pageable pageable) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see od.providers.events.EventProvider#getEventsForCourseAndUser(java.lang.String, java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
   */
  @Override
  public Page<Event> getEventsForCourseAndUser(String tenantId, String courseId, String userId, Pageable pageable) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see od.providers.events.EventProvider#postEvent(com.fasterxml.jackson.databind.JsonNode, java.lang.String)
   */
  @Override
  public JsonNode postEvent(JsonNode marshallableObject, String tenantId) throws ProviderException, MethodNotImplementedException {
    // TODO Auto-generated method stub
    return null;
  }

}
