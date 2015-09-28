/**
 * 
 */
package od.providers.modeloutput.lap;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.framework.model.Card;
import od.framework.model.ContextMapping;
import od.providers.BaseProvider;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.api.PageWrapper;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;
import od.providers.modeloutput.ModelOutputProvider;
import od.repository.ContextMappingRepositoryInterface;
import od.repository.ProviderDataRepositoryInterface;

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
  private static final String BASE = "APEREO_LAP";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
  
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;
  private RestTemplate restTemplate;
  
  @PostConstruct
  public void init() {
    
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "LAP Base URL", "LABEL_LAP_BASE_URL", false);

    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    options.add(key);
    options.add(secret);
    options.add(baseUrl);
    
    providerConfiguration = new DefaultProviderConfiguration(options);
  }
  
  private PageImpl<ModelOutput> fetch(Map<String, String> urlVariables, Pageable pageable, String uri) {
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    String url = getUrl(providerData.findValueForKey("base_url"), uri, pageable);
    
    restTemplate = new RestTemplate();
    
    ParameterizedTypeReference<PageWrapper<ModelOutputImpl>> responseType = new ParameterizedTypeReference<PageWrapper<ModelOutputImpl>>() {};
    
    PageWrapper<ModelOutputImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, 
        new HttpEntity(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret"))), 
        responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<ModelOutput> output = null;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      output = new LinkedList<ModelOutput>(pageWrapper.getContent());
    }
    
    return new PageImpl<ModelOutput>(output, pageable, pageWrapper.getTotalElements());

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

  @Override
  public Page<ModelOutput> getModelOutputForCourse(ProviderOptions options, String course, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", course);
    
    return fetch(urlVariables, pageable, "/api/output/course/{id}");
  }

  @Override
  public Page<ModelOutput> getModelOutputForStudent(ProviderOptions options, String student, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", student);
    
    return fetch(urlVariables, pageable, "/api/output/student/{id}");
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
