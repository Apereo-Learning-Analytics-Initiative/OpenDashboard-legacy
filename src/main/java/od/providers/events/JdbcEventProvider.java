/**
 *  
 * @author	Marist College Data Science (Kaushik, Sumit, Joy, Ed)
 * @version	0.1
 * @since	2017-06-01
 */

package od.providers.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.exception.MethodNotImplementedException;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.jdbc.JdbcClient;
import od.providers.jdbc.JdbcProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component("events_jdbc")
public class JdbcEventProvider extends JdbcProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcEventProvider.class);

  private static final String KEY = "events_jdbc";
  private static final String BASE = "JDBC_EVENTS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
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
  public ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId, boolean studentsOnly) throws ProviderException {
	  ClassEventStatistics classEventsStats = null;
	  Integer numEvents = 0;
	  Integer numStudents = 0;
	  
	  try
	  {
		JdbcClient client = new JdbcClient(mongoTenantRepository.findOne(tenantId).findByKey(KEY));
		try {
			// Get Summary data first, could be big, let the DB do the grouping, may be more summary data in the future
			String SQL = "SELECT * FROM VW_OD_EV_SUMMARYDATA WHERE  CLASSSOURCEDID = ?";
			ResultSet Rs = client.getData(SQL, classSourcedId);
			if (Rs.next()){
				numEvents = Rs.getInt("NUMEVENTS");
				numStudents = Rs.getInt("NUMSTUDENTS");
			}
			if (!Rs.isClosed()){
				  Rs.close();
			}
	
			// Get Events by Date
			SQL = "SELECT * FROM VW_OD_EV_COUNTBYDATE WHERE CLASSSOURCEDID = ? ORDER BY EVENTDATE";
			Rs = client.getData(SQL, classSourcedId);
			Map<String, Long> eventCountByDate = new HashMap<String, Long>();
			while (Rs.next()){
			  eventCountByDate.put(Rs.getString("EVENTDATE"), Rs.getLong("NUMEVENTS"));
			}
			if (!Rs.isClosed()){
				  Rs.close();
			}
	
			// Get Events by Date
			SQL = "SELECT * FROM VW_OD_EV_COUNTBYDATEBYSTUDENT WHERE  CLASSSOURCEDID = ? ORDER BY EVENTDATE";
			Rs = client.getData(SQL, classSourcedId);
			Map<String, Map<String, Long>> eventCountGroupedByDateAndStudent = new HashMap<String, Map<String, Long>> ();
			
			String lastEventDate = "NONE";
			Map<String, Long> studentEvents = null; 
			String currentEventDate = null;
			Long eventCount = null;
			String studentId = null;
			while (Rs.next()){
				currentEventDate = Rs.getString("EVENTDATE");
				eventCount = Rs.getLong("NUMEVENTS");
				studentId = Rs.getString("STUDENTID");
				
				if (!lastEventDate.equalsIgnoreCase(currentEventDate)){
					if (!lastEventDate.equalsIgnoreCase("NONE")){
						eventCountGroupedByDateAndStudent.put(lastEventDate, studentEvents);
					}
					studentEvents = new HashMap<String, Long>();
				}
				
				studentEvents.put(studentId, eventCount);    	  
				lastEventDate = currentEventDate;
			}
			if (!Rs.isClosed()){
				  Rs.close();
			}
			if (studentEvents != null && studentEvents.size() > 0){
				eventCountGroupedByDateAndStudent.put(lastEventDate, studentEvents);
			}
			classEventsStats= new ClassEventStatistics.Builder()
									.withClassSourcedId(classSourcedId)
									.withTotalEvents(numEvents)
									.withTotalStudentEnrollments(numStudents)
									.withEventCountGroupedByDate(eventCountByDate)
									.withEventCountGroupedByDateAndStudent(eventCountGroupedByDateAndStudent)
									.build();
		} catch (Exception e){
			log.error(e.getMessage());
		}
		client.close();
	  }
	  catch(Exception e)
	  {
		  log.error(e.getMessage());
	  }
	  return classEventsStats;	  
    }

  @Override
  public Page<Event> getEventsForUser(String tenantId, String userSourcedId, Pageable pageable) throws ProviderException {
	    ProviderData providerData = mongoTenantRepository.findOne(tenantId).findByKey(KEY);
	    List<Event> userEvents = new ArrayList<Event>();
	    try {
		    JdbcClient client = new JdbcClient(providerData);
	    	String SQL = "SELECT * FROM VW_OD_EV_USER WHERE USERSOURCEDID = ?";
	    	try {
		        ResultSet Rs = client.getData(SQL, userSourcedId);
		    	while (Rs.next()) 
		       	 {
		    		 EventImpl eventImpl = new EventImpl();
		    	      eventImpl.setActor(Rs.getString("USERSOURCEDID"));
		    	      eventImpl.setContext(Rs.getString("CLASSSOURCEDID"));
		    	      eventImpl.setId(Rs.getString("ID"));
		    	      eventImpl.setObject(Rs.getString("OBJECT"));
		    	      eventImpl.setObjectType(Rs.getString("OBJECTTYPE"));
		    	      eventImpl.setSourcedId(Rs.getString("USERSOURCEDID"));
		    	      eventImpl.setVerb(Rs.getString("VERB"));
		    	      eventImpl.setTimestamp(Rs.getString("EVENT_DATE"));
		    	      
		    	      userEvents.add(eventImpl);
		       	 }
				if (!Rs.isClosed()){
					  Rs.close();
				}
		    }
		    catch (SQLException e) {
		    	log.error(e.getMessage());
			}
	    	client.close();
	    } catch(Exception e){
	    	log.error(e.getMessage());
	    }
	    return new PageImpl<>(userEvents);
  }

  @Override
  public Page<Event> getEventsForCourse(String tenantId, String classSourcedId, Pageable pageable) throws ProviderException {

	    ProviderData providerData = mongoTenantRepository.findOne(tenantId).findByKey(KEY);
	    List<Event> userEvents = new ArrayList<Event>();
	    try {
		    JdbcClient client = new JdbcClient(providerData);
		    try {
		    	String SQL = "SELECT * FROM VW_OD_EV_COURSE WHERE CLASSSOURCEDID = ?";
		        ResultSet Rs = client.getData(SQL, classSourcedId);
		        
		    	while (Rs.next()) 
		       	 {
		    		 EventImpl eventImpl = new EventImpl();
		    	      eventImpl.setActor(Rs.getString("USERSOURCEDID"));
		    	      eventImpl.setContext(Rs.getString("CLASSSOURCEDID"));
		    	      eventImpl.setId(Rs.getString("ID"));
		    	      eventImpl.setObject(Rs.getString("OBJECT"));
		    	      eventImpl.setObjectType(Rs.getString("OBJECTTYPE"));
		    	      eventImpl.setSourcedId(Rs.getString("USERSOURCEDID"));
		    	      eventImpl.setVerb(Rs.getString("VERB"));
		    	      eventImpl.setTimestamp(Rs.getString("EVENT_DATE"));
		    	      
		    	      userEvents.add(eventImpl);
		       	 }
				if (!Rs.isClosed()){
					  Rs.close();
				}
		    }
		    catch (SQLException e) {
		    	log.error(e.getMessage());
			}
		    client.close();
	    } catch (Exception e){
	    	log.error(e.getMessage());
	    }
	    return new PageImpl<>(userEvents);
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(String tenantId, String classSourcedId, String userSourcedId, Pageable pageable) throws ProviderException {

    ProviderData providerData = mongoTenantRepository.findOne(tenantId).findByKey(KEY);
    JdbcClient client = new JdbcClient(providerData);
    List<Event> userEvents = new ArrayList<Event>();
    try {
    	String SQL = "SELECT * FROM VW_OD_EV_COURSEANDUSER WHERE CLASSSOURCEDID = ? AND USERSOURCEDID = ?";
        ResultSet Rs = client.getData(SQL, classSourcedId, userSourcedId);
        
    	while (Rs.next()) 
       	 {
    		 EventImpl eventImpl = new EventImpl();
	   	      eventImpl.setActor(Rs.getString("USERSOURCEDID"));
	   	      eventImpl.setContext(Rs.getString("CLASSSOURCEDID"));
    	      eventImpl.setId(Rs.getString("ID"));
    	      eventImpl.setObject(Rs.getString("OBJECT"));
    	      eventImpl.setObjectType(Rs.getString("OBJECTTYPE"));
    	      eventImpl.setSourcedId(Rs.getString("USERSOURCEDID"));
    	      eventImpl.setVerb(Rs.getString("VERB"));
    	      eventImpl.setTimestamp(Rs.getString("EVENT_DATE"));
    	      
    	      userEvents.add(eventImpl);
       	 }
		if (!Rs.isClosed()){
			  Rs.close();
		}
    }
    catch (SQLException e) {
		log.error(e.getMessage());
	}
    
    return new PageImpl<>(userEvents);
  }

  @Override
  public JsonNode postEvent(JsonNode marshallableObject, String tenantId) throws ProviderException, MethodNotImplementedException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}