/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package od.providers.events;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Actor;
import gov.adlnet.xapi.model.Context;
import gov.adlnet.xapi.model.ContextActivities;
import gov.adlnet.xapi.model.IStatementObject;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.StatementResult;
import gov.adlnet.xapi.model.Verb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.exception.MethodNotImplementedException;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.learninglocker.LearningLockerProvider;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ggilbert
 *
 */
@Component("events_learninglocker")
public class LearningLockerXApiEventProvider extends LearningLockerProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(LearningLockerXApiEventProvider.class);

  private static final String KEY = "events_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_EVENT";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultLearningLockerConfiguration();
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
  
  private static class MapBuilder<V> {
    private HashMap<String, V> m = new HashMap<String, V>();

    public MapBuilder<V> put(String key, V value) {
        m.put(key, value);
        return this;
    }

    public HashMap<String, V> build() {
        return m;
    }
  }
  
  private static final String EN_US = "en-US";
  private static final String EN_GB = "en-GB";

//  public void post() throws IOException {
//
//    StatementClient statementClient = new StatementClient("https://jisc.learninglocker.net/data/xAPI/", "4de4712289f6ce2e3404b7e92e71c919b480604a", "7deabdfc523fd12acde83c51947f6e2c1653cd36");
//    
//    RestTemplate rt = getRestTemplate(new ProviderData());
//    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth("3f2c9b5714eb9c333f843cfa89de0b7a97817150", "8b7841d35e308c8907ec086061652e14f9ac95d8"));
//    String studentModuleInstanceUrl = buildUrl("http://78.136.52.242", STUDENT_MODULE_INSTANCE_URI);
//    MultiValueMap<String, String> studentModuleInstanceParams = new LinkedMultiValueMap<String, String>();
//    studentModuleInstanceParams.add("populate", "student");
//    URI studentModuleInstanceURI = buildUri(studentModuleInstanceUrl, studentModuleInstanceParams);
//    
//    LearningLockerStudentModuleInstance [] studentModuleInstances 
//    = rt.exchange(studentModuleInstanceURI, 
//      HttpMethod.GET, headers, LearningLockerStudentModuleInstance[].class).getBody();
//    
//    if (studentModuleInstances != null) {
//      for (LearningLockerStudentModuleInstance smi : studentModuleInstances) {
//        Statement statement = new Statement();
//        
//        statement = new Statement();
//        statement.setVersion("1.0.0");
//        Account account = new Account();
//        Agent agent = new Agent();
//        account.setHomePage("https://github.com/jiscdev/analytics-udd/blob/master/predictive-core.md#student_id");
//        account.setName(smi.getSTUDENT_ID());
//        agent.setAccount(account);
//
//        statement.setActor(agent);
//        statement.setVerb(new Verb(
//                "http://activitystrea.ms/schema/1.0/receive",
//                new MapBuilder<String>().put(EN_GB, "receive").put(EN_US, "receive").build()
//        ));
//        
//        Activity a = new Activity();
//        a.setId("https://lap.jisc.ac.uk/earlyAlert/unicon/id");
//        ActivityDefinition ad = new ActivityDefinition();
//        ad.setType("http://activitystrea.ms/schema/1.0/alert");
//        ad.setName(new MapBuilder<String>().put(EN_GB, "An early alert").put(EN_US, "An early alert").build());
//        ad.setDescription(new MapBuilder<String>().put(EN_GB, "An early alert").put(EN_US, "An early alert").build());
//        ad.setMoreInfo("todo://urltoviewthealert");
//
//        ad.setExtensions(new MapBuilder<JsonElement>().put("https://lap.jisc.ac.uk/earlyAlert/type", new JsonPrimitive("UNICON")).build());
//        a.setDefinition(ad);
//
//        statement.setObject(a);
//        
//        ActivityDefinition adef = new ActivityDefinition();
//        adef.setType("http://adlnet.gov/expapi/activities/module");
//        adef.setDescription(new MapBuilder<String>().put(EN_US, "Jisc Module Instance").build());
//        adef.setName(new MapBuilder<String>().put(EN_US, "Jisc Module Instance").build());
//        adef.setExtensions(new MapBuilder<JsonElement>().put("https://lap.jisc.ac.uk/taxonomy", new JsonPrimitive("MOD_INSTANCE")).build());
//        Activity activity = new Activity(String.format("https://github.com/jiscdev/analytics-udd/blob/master/predictive-core.md#mod_instance/%s",smi.getMOD_INSTANCE_ID()), adef);
//        ContextActivities ca = new ContextActivities();
//        ca.setGrouping(new ArrayList<Activity>(Collections.singletonList(activity)));
//        Context context = new Context();
//        context.setContextActivities(ca);
//
//        statement.setContext(context);
//
//        // build just enough of the structure of the result to serve as a placeholder.
//        // we'll update the result contents as we iterate over records.
//        Result result = new Result();
//        JsonObject obj = new JsonObject();
//        
//        final String[] risks = {"NO RISK", "LOW RISK", "MEDIUM RISK", "HIGH RISK"};
//        Random random = new Random();
//        
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/modelRiskConfidence", new JsonPrimitive(risks[random.nextInt(risks.length)]));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/rContentRead", new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextInt(0, 200 + 1))));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/gpaCumulative",new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextDouble(0.0,4.0))));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/rmnScore",new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextInt(0, 200 + 1))));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/rForumPost",new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextInt(0, 200 + 1))));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/rAsnSub", new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextInt(0, 200 + 1))));
//        obj.add("https://lap.jisc.ac.uk/earlyAlert/rSessions", new JsonPrimitive(String.valueOf(ThreadLocalRandom.current().nextDouble(0.0,2.0))));
//
//        result.setExtensions(obj);
//        statement.setResult(result);
//
//        Actor authority = new Agent();
//        authority.setName("Unicon");
//        authority.setMbox("mailto:hello@unicon.net");
//        
//        statementClient.postStatement(statement);
//      }
//    }
//
//  }

  private Page<Event> fetch(String tenantId, String more) throws IOException {
    Page<Event> page = null;
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    
    ProviderData providerData = tenant.findByKey(KEY);
    String uri = providerData.findValueForKey("base_url");
    String user = providerData.findValueForKey("key");
    String password = providerData.findValueForKey("secret");

    StatementClient statementClient = new StatementClient(uri, user, password);
    StatementResult results = statementClient.getStatements(more);
    
    if (results != null && results.getStatements() != null && !results.getStatements().isEmpty()) {
      List<Statement> statements = results.getStatements();
      List<Event> events = new ArrayList<>();
      for (Statement statement : statements) {
        Event event = toEvent(statement);
        if (event != null) {
          events.add(event);
        }
      }
      
      page = new PageImpl<>(events);
    }

    return page;
  }

  @Override
  public Page<Event> getEventsForUser(String tenantId, String userId, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(tenantId, more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
  }

  @Override
  public Page<Event> getEventsForCourse(String tenantId, String courseId, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(tenantId, more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(String tenantId, String courseId, String userId, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(tenantId, more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
  }
  
  @Override
  public ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId, boolean studentsOnly) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  private Event toEvent(gov.adlnet.xapi.model.Statement statement) {
    EventImpl event = null;
    if (statement != null) {
       event = new EventImpl();
       
       event.setActor(parseActorXApi(statement));
       event.setContext(parseContextXApi(statement));
       event.setEventFormatType("XAPI");
       
       Map<String,String> object = parseObjectXApi(statement);
       if (object != null && !object.isEmpty()) {
         event.setObject(object.get("ID"));
         event.setObjectType(object.get("TYPE"));
       }
       
       //TODO
       event.setOrganization(null);
       event.setRaw(statement.serialize().getAsString());
       event.setSourcedId(statement.getId());
       event.setTimestamp(statement.getTimestamp());
       event.setVerb(parseVerbXApi(statement));
     }
    return event;
   }
  
  private String parseContextXApi(Statement xapi) {
    String context = null;
    
    Context xApiContext = xapi.getContext();
    if (xApiContext != null) {
      ContextActivities xApiContextActivities = xApiContext.getContextActivities();
      if (xApiContextActivities != null) {
        List<Activity> parentContext = xApiContextActivities.getParent();
        if (parentContext != null && !parentContext.isEmpty()) {
          for (Activity activity : parentContext) {
            context = activity.getId();
            break;
          }
        }
      }
    }

    return context;
  }
  
  private String parseActorXApi(gov.adlnet.xapi.model.Statement xapi) {
    String actor = null;
    
    Actor xApiActor = xapi.getActor();
    if (xApiActor != null) {
      String mbox = xApiActor.getMbox();
      if (StringUtils.isNotBlank(mbox)) {
        actor = StringUtils.substringBetween(mbox, "mailto:", "@");
      }
    }

    return actor;
  }
  
  private String parseVerbXApi(gov.adlnet.xapi.model.Statement xapi) {
    String verb = null;
    
    Verb xApiVerb = xapi.getVerb();
    if (xApiVerb != null) {
      Map<String,String> display = xApiVerb.getDisplay();
      if (display != null && !display.isEmpty()) {
        verb = display.get("en-US");
      }
      
      if (StringUtils.isBlank(verb)) {
        String id = xApiVerb.getId();
        if (StringUtils.isNotBlank(id)) {
          verb = StringUtils.substringAfterLast(id, "/");
        }
      }
    }
    
    return verb;
  }
  
  private Map<String,String> parseObjectXApi(gov.adlnet.xapi.model.Statement xapi) {
    Map<String,String> objectIdandType = null;
    
    IStatementObject xApiObject = xapi.getObject();
    if (xApiObject != null) {
      
      objectIdandType = new HashMap<>();
      objectIdandType.put("TYPE", xApiObject.getObjectType());
    }
    
    return objectIdandType;
  }

	@Override
	public JsonNode postEvent(JsonNode marshallableObject, String tenantId)
			throws ProviderException, MethodNotImplementedException {
		
			throw new MethodNotImplementedException();
	}


}
