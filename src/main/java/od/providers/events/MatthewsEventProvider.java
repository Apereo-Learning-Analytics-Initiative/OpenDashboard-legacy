/**
 * 
 */
package od.providers.events;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import od.exception.MethodNotImplementedException;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.matthews.MatthewsClient;
import od.providers.matthews.MatthewsProvider;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ggilbert
 *
 */
@Component("events_matthews")
public class MatthewsEventProvider extends MatthewsProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(MatthewsEventProvider.class);
  
  private static final String KEY = "events_matthews";
  private static final String BASE = "MATTHEWS_EVENTS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  @Autowired private MongoTenantRepository mongoTenantRepository;

  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultMatthewsConfiguration();
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

  @Override
  public ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId, boolean studentsOnly) throws ProviderException {
    Tenant tenant = mongoTenantRepository.findOne(tenantId);    
    ProviderData providerData = tenant.findByKey(KEY);

    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/").concat(classSourcedId).concat("/events/stats");
    if (!studentsOnly) {
      endpoint = endpoint.concat("?studentsOnly=false");
    }
    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ClassEventStatistics ces = null;
    
    try {
      ResponseEntity<ClassEventStatistics> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), ClassEventStatistics.class);
      ces = response.getBody();
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    
    if (ces == null) {
      ces 
        = new ClassEventStatistics.Builder()
          .withClassSourcedId(classSourcedId)
          .withTotalEvents(0)
          .build();
    }
    
    return ces;
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

    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/").concat(courseId).concat("/events/user/").concat(userId);
    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<unicon.matthews.caliper.Event[]> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), unicon.matthews.caliper.Event[].class);
    
    unicon.matthews.caliper.Event [] events = response.getBody();
    
    Function<unicon.matthews.caliper.Event, Event> myFunction = new Function<unicon.matthews.caliper.Event, Event>() {
      public Event apply(unicon.matthews.caliper.Event t) {
        EventImpl event = new EventImpl();
        
        String actor = null;
        String context = null;
        String eventFormatType = null;
        String object = null;
        String objectType = null;
        String organization = null;
        String raw = null;
        String timestamp = null;
        String verb = null;
        
        if (t.getAgent() != null) {
          actor = t.getAgent().getId();
        }
        
        if (t.getGroup() != null) {
          context = t.getGroup().getId();
        }
        
        if (t.getObject() != null) {
          object = t.getObject().getId();
          objectType = t.getObject().getType();
        }
        
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        timestamp = t.getEventTime().format(dtf);
        
        verb = t.getAction();
      
        event.setActor(actor);
        event.setContext(context);
        event.setEventFormatType(eventFormatType);
        event.setId(t.getId());
        event.setObject(object);
        event.setObjectType(objectType);
        event.setOrganization(organization);
        event.setRaw(raw);
        event.setSourcedId(t.getId());
        event.setTimestamp(timestamp);
        event.setVerb(verb);
      
        return event;
      }
    };
    
    if (events != null && events.length > 0) {
      List<Event> eventconverted = Arrays.asList(events).stream()
          .map(myFunction)
          .collect(Collectors.<Event> toList());
      
      return new PageImpl<>(eventconverted);
    }
    else {
      return new PageImpl<>(new ArrayList<Event>());
    }
  }

  @Override
  public JsonNode postEvent(JsonNode marshallableObject, String tenantId) throws ProviderException, MethodNotImplementedException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
