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
package od.providers.events.openlrs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.BaseProvider;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.api.PageWrapper;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;
import od.providers.events.EventProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
@Component("events_openlrs")
public class OpenLRSEventProvider extends BaseProvider implements EventProvider {

  private static final Logger log = LoggerFactory.getLogger(OpenLRSEventProvider.class);

  private static final String KEY = "events_openlrs";
  private static final String BASE = "OPEN_LRS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  private RestTemplate restTemplate;
  
  @PostConstruct
  public void init() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "OpenLRS Base URL", "LABEL_OPENLRS_BASE_URL", false);
    options.add(key);
    options.add(secret);
    options.add(baseUrl);

    providerConfiguration = new DefaultProviderConfiguration(options);
  }
  
  private PageImpl<Event> fetch(String tenantId, Map<String, String> urlVariables, Pageable pageable, String uri) {
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    ProviderData providerData = tenant.findByKey(KEY);

    String url = getUrl(providerData.findValueForKey("base_url"), uri, pageable);
    
    restTemplate = new RestTemplate();
    
    ParameterizedTypeReference<PageWrapper<EventImpl>> responseType = new ParameterizedTypeReference<PageWrapper<EventImpl>>() {};
    
    PageWrapper<EventImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<Event> events = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      events = new LinkedList<Event>(pageWrapper.getContent());
    }
    
    return new PageImpl<Event>(events, pageable, pageWrapper.getPage().getTotalElements());

  }
  
  @Override
  public Page<Event> getEventsForCourse(ProviderOptions options, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<>();
    urlVariables.put("contextId", options.getCourseId());

    return fetch(options.getTenantId(), urlVariables, pageable, "/api/context/{contextId}");
  }

  @Override
  public Page<Event> getEventsForUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    
    Map<String, String> urlVariables = new HashMap<>();
    urlVariables.put("userId", options.getUserId());
    
    return fetch(options.getTenantId(), urlVariables, pageable, "/api/user/{userId}");
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    
    Map<String, String> urlVariables = new HashMap<>();
    urlVariables.put("userId", options.getUserId());
    urlVariables.put("contextId", options.getCourseId());
    
    return fetch(options.getTenantId(), urlVariables, pageable, "/api/user/{userId}/context/{contextId}");
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
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

}
