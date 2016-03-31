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
package od.providers.events.learninglocker;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;
import od.providers.events.EventProvider;
import od.providers.learninglocker.LearningLockerProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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
    LinkedList<ProviderConfigurationOption> options = new LinkedList<>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "Learning Locker xAPI Base URL", "LABEL_LL_BASE_URL", false);
    options.add(key);
    options.add(secret);
    options.add(baseUrl);

    providerConfiguration = new DefaultProviderConfiguration(options);
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
  public Page<Event> getEventsForUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(options.getTenantId(), more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
  }

  @Override
  public Page<Event> getEventsForCourse(ProviderOptions options, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(options.getTenantId(), more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    Page<Event> events = null;
    
    String more = null;
    
    try {
      events = fetch(options.getTenantId(), more);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }
    
    return events;
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
       event.setSourceId(statement.getId());
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


}
