/**
 * 
 */
package od.providers.events.openlrs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.framework.model.Card;
import od.framework.model.ContextMapping;
import od.providers.BaseProvider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.api.PageWrapper;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.events.EventProvider;
import od.repository.ContextMappingRepositoryInterface;

import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
  private static final String NAME = "OpenLRS";
  private ProviderConfiguration providerConfiguration;
  
  @Autowired private ContextMappingRepositoryInterface contextMappingRepository;
  private RestTemplate restTemplate;
  
  @PostConstruct
  public void init() {
    
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    
    providerConfiguration = new DefaultProviderConfiguration(options);
  }
  
  @Override
  public Page<Event> getEventsForCourse(ProviderOptions options, Pageable pageable) throws ProviderException {
        
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("contextId", options.getCourseId());
    
    ContextMapping contextMapping = contextMappingRepository.findOne(options.getContextMappingId());
    Card card = contextMapping.findCard(options.getCardId());
    Map<String,Object> config = card.getConfig();
    
    String url = getUrl((String)config.get("url"), "/api/context/{contextId}", pageable);
    
    restTemplate = new RestTemplate();
    
    ParameterizedTypeReference<PageWrapper<EventImpl>> responseType = new ParameterizedTypeReference<PageWrapper<EventImpl>>() {};
    
    PageWrapper<EventImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth((String)config.get("key"), (String)config.get("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<Event> events = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      events = new LinkedList<Event>(pageWrapper.getContent());
    }
    
    return new PageImpl<Event>(events, pageable, pageWrapper.getTotalElements());
  }

  @Override
  public Page<Event> getEventsForUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("userId", options.getUserId());
    
    ContextMapping contextMapping = contextMappingRepository.findOne(options.getContextMappingId());
    Card card = contextMapping.findCard(options.getCardId());
    Map<String,Object> config = card.getConfig();
    String url = getUrl((String)config.get("url"), "/api/user/{userId}", pageable);

    restTemplate = new RestTemplate();
    ParameterizedTypeReference<PageWrapper<EventImpl>> responseType = new ParameterizedTypeReference<PageWrapper<EventImpl>>() {};

    PageWrapper<EventImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth((String)config.get("key"), (String)config.get("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<Event> events = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      events = new LinkedList<Event>(pageWrapper.getContent());
    }
    
    return new PageImpl<Event>(events, pageable, pageWrapper.getTotalElements());
  }

  @Override
  public Page<Event> getEventsForCourseAndUser(ProviderOptions options, Pageable pageable) throws ProviderException {
    
    
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("userId", options.getUserId());
    urlVariables.put("contextId", options.getCourseId());
    
    ContextMapping contextMapping = contextMappingRepository.findOne(options.getContextMappingId());
    Card card = contextMapping.findCard(options.getCardId());
    Map<String,Object> config = card.getConfig();
    String url = getUrl((String)config.get("url"), "/api/user/{userId}/context/{contextId}", pageable);
    
    restTemplate = new RestTemplate();
    ParameterizedTypeReference<PageWrapper<EventImpl>> responseType = new ParameterizedTypeReference<PageWrapper<EventImpl>>() {};

    PageWrapper<EventImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth((String)config.get("key"), (String)config.get("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<Event> events = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      events = new LinkedList<Event>(pageWrapper.getContent());
    }
    
    return new PageImpl<Event>(events, pageable, pageWrapper.getTotalElements());
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
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

}
