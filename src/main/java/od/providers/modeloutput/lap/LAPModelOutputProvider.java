/**
 * 
 */
package od.providers.modeloutput.lap;

import java.util.Arrays;
import java.util.Collections;
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
import od.providers.config.KeyValueProviderConfigurationOption;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.events.openlrs.OpenLRSEventProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.repository.ContextMappingRepositoryInterface;

import org.apereo.lai.Event;
import org.apereo.lai.ModelOutput;
import org.apereo.lai.impl.EventImpl;
import org.apereo.lai.impl.ModelOutputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component("modeloutput_lap")
public class LAPModelOutputProvider extends BaseProvider implements ModelOutputProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LAPModelOutputProvider.class);
  
  private static final String KEY = "modeloutput_lap";
  private static final String NAME = "Apereo Learning Analytics Processor";
  private ProviderConfiguration providerConfiguration;
  
  @Autowired private ContextMappingRepositoryInterface contextMappingRepository;
  private RestTemplate restTemplate;
  
  @PostConstruct
  public void init() {
    ProviderConfigurationOption key = new KeyValueProviderConfigurationOption("OAuth Consumer Key", null, ProviderConfigurationOption.TEXT_TYPE, true);
    ProviderConfigurationOption secret = new KeyValueProviderConfigurationOption("Secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true);
    ProviderConfigurationOption baseUrl = new KeyValueProviderConfigurationOption("Base URL", null, ProviderConfigurationOption.URL_TYPE, true);
    
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
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
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

  @Override
  public Page<ModelOutput> getModelOutputForCourse(ProviderOptions options, String course, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", course);
    
    ContextMapping contextMapping = contextMappingRepository.findOne(options.getContextMappingId());
    Card card = contextMapping.findCard(options.getCardId());
    Map<String,Object> config = card.getConfig();
    
    String url = getUrl((String)config.get("url"), "/api/output/course/{id}", pageable);
    
    restTemplate = new RestTemplate();
    
    ParameterizedTypeReference<PagedResources<ModelOutputImpl>> responseType = new ParameterizedTypeReference<PagedResources<ModelOutputImpl>>() {};
    
    PagedResources<ModelOutputImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth((String)config.get("key"), (String)config.get("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<ModelOutput> modeloutput = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      modeloutput = new LinkedList<ModelOutput>(pageWrapper.getContent());
    }
    
    return new PageImpl<ModelOutput>(modeloutput, pageable, pageWrapper.getMetadata().getTotalElements());
  }

  @Override
  public Page<ModelOutput> getModelOutputForStudent(ProviderOptions options, String student, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", student);
    
    ContextMapping contextMapping = contextMappingRepository.findOne(options.getContextMappingId());
    Card card = contextMapping.findCard(options.getCardId());
    Map<String,Object> config = card.getConfig();
    
    String url = getUrl((String)config.get("url"), "/api/output/student/{id}", pageable);
    
    restTemplate = restTemplate();
    
    ParameterizedTypeReference<PagedResources<ModelOutputImpl>> responseType = new ParameterizedTypeReference<PagedResources<ModelOutputImpl>>() {};
    
    PagedResources<ModelOutputImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth((String)config.get("key"), (String)config.get("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<ModelOutput> modeloutput = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      modeloutput = new LinkedList<ModelOutput>(pageWrapper.getContent());
    }
    
    return new PageImpl<ModelOutput>(modeloutput, pageable, pageWrapper.getMetadata().getTotalElements());
  }
  
  private RestTemplate restTemplate() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new Jackson2HalModule());

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    //converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
    converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json"));
    converter.setObjectMapper(mapper);
    return new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
   }

}
