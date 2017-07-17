/**
 *
 */
package od.providers.events;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import od.exception.MethodNotImplementedException;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.events.DemoEventProvider.Verb;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

@Component("events_jdbc")
public class JdbcEventProvider extends JdbcProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcEventProvider.class);

  private static final String KEY = "events_jdbc";
  private static final String BASE = "JDBC_EVENTS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  private Set<Event> demoClass1Events;
  private Map<String, Set<Event>> classEventsMap;
  private Map<String, ClassEventStatistics> classEventsStatsMap;
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultJdbcConfiguration();
  }

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
  class Verb {
	    private String verb;
	    private double weight;
	    public Verb(double weight, String verb) {
	      this.weight = weight;
	      this.verb = verb;
	    }
	    public String getVerb() {
	      return verb;
	    }
	    public double getWeight() {
	      return weight;
	    }
	  }
  @Override
  public ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId) throws ProviderException {
	  ClassEventStatistics classEventsStats = null;
	  try
	  {
		  Tenant tenant = mongoTenantRepository.findOne(tenantId);
	      ProviderData providerData = tenant.findByKey(KEY);
	      JdbcClient client = new JdbcClient(providerData);
	      
	      // *********** Debug, setting class source ID to iLearn ID for 
	      String origClassSourcedId = classSourcedId;
	      classSourcedId = "MATH_115L_118_201440";
	      // *********** remove the above lines before moving to production or testing
	      
	      // Get Summary data
	      String SQL = "SELECT * FROM VW_OD_EV_SummaryData WHERE  classSourcedId = '" 
	    		  		+ classSourcedId +  "'";
		  ResultSet Rs = client.getData(SQL);
		  while(Rs.next())
		  {
		  Integer numEvents = Rs.getInt("numEvents");
		  Integer numStudents = Rs.getInt("numStudents");

		  Rs.close();
		  
		  // Get Events by Date
	      SQL = "SELECT * FROM VW_OD_EV_CountByDate WHERE  classSourcedId = '" 
	    		  		+ classSourcedId +  "' ORDER BY eventDate";
		  Rs = client.getData(SQL);
		  Map<String, Long> eventCountByDate = new HashMap<String, Long>();
	      while (Rs.next())
		 {
			  eventCountByDate.put(Rs.getString("eventDate"), Rs.getLong("numEvents"));
		 }

		  // Get Events by Date
	      SQL = "SELECT * FROM VW_OD_EV_CountByDateByStudent WHERE  classSourcedId = '" 
	    		  		+ classSourcedId +  "' ORDER BY eventDate";
		  Rs = client.getData(SQL);
		  Map<String, Map<String, Long>> eventCountGroupedByDateAndStudent = new HashMap<String, Map<String, Long>> ();
		  
		  String lastEventDate = "NONE";
		  Map<String, Long> studentEvents = null; 
		  String currentEventDate = null;
		  Long eventCount = null;
		  String studentId = null;
	      while (Rs.next())
	 	  {
	    	  currentEventDate = Rs.getString("eventDate");
	    	  eventCount = Rs.getLong("numEvents");
	    	  studentId = Rs.getString("studentId");
	    	  
	    	  if (!lastEventDate.equalsIgnoreCase(currentEventDate))
	    	  {
	    		  if (!lastEventDate.equalsIgnoreCase("NONE"))
	    		  {
	    			  eventCountGroupedByDateAndStudent.put(lastEventDate, studentEvents);
	    		  }
	    		  studentEvents = new HashMap<String, Long>();
	    	  }
	    	  
	    	  studentEvents.put(studentId, eventCount);    	  
	    	  lastEventDate = currentEventDate;
	 	  }
	      Rs.close();
	      if (studentEvents.size() > 0)
	      {
	    	  eventCountGroupedByDateAndStudent.put(lastEventDate, studentEvents);
	      }
	
	      
	      // *********** Debug, put class source id back 
		  classSourcedId = origClassSourcedId;
	      // *********** remove the above lines before moving to production or testing
	
		  classEventsStats 
		      = new ClassEventStatistics.Builder()
		        .withClassSourcedId(classSourcedId)
		        .withTotalEvents(numEvents)
		        .withTotalStudentEnrollments(numStudents)
		        .withEventCountGroupedByDate(eventCountByDate)
		        .withEventCountGroupedByDateAndStudent(eventCountGroupedByDateAndStudent)
		        .build();
	  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return classEventsStats;	  
   
    }

  @Override
  public Page<Event> getEventsForUser(String tenantId, String userId, Pageable pageable) throws ProviderException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public Page<Event> getEventsForCourse(String tenantId, String courseId, Pageable pageable) throws ProviderException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(String tenantId, String courseId, String userId, Pageable pageable) throws ProviderException {
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    ProviderData providerData = tenant.findByKey(KEY);
    JdbcClient client = new JdbcClient(providerData);
    List<Event> userEvents = new ArrayList<Event>();
    try {
    	String SQL1 = "select * from VW_EV_EVENTS_PROVIDER";
        ResultSet Rs = client.getData(SQL1);
        
    	while (Rs.next()) 
       	 {
    		 EventImpl eventImpl = new EventImpl();
    	      eventImpl.setActor(userId);
    	      eventImpl.setContext(courseId);
    	      eventImpl.setId(UUID.randomUUID().toString());
    	      eventImpl.setObject(Rs.getString("OBJ"));
    	      eventImpl.setObjectType(Rs.getString("OBJTYPE"));
    	      eventImpl.setSourcedId(Rs.getString("SOURCEID"));
    	      eventImpl.setVerb(Rs.getString("VERB"));
    	      eventImpl.setTimestamp(Rs.getString("TIMESTAMP"));
    	      
    	      userEvents.add(eventImpl);    
       	 }
    }
    catch (SQLException e) {
		e.printStackTrace();
	}
    
    
    return new PageImpl<>(userEvents);
  }

  @Override
  public JsonNode postEvent(JsonNode marshallableObject, String tenantId) throws ProviderException, MethodNotImplementedException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}