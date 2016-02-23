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
/**
 * 
 */
package od.providers.modeloutput.lap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import od.repository.ProviderDataRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.ModelOutput;
import org.apereo.lai.impl.ModelOutputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
  
  private static final String LAP_OAUTH_TOKEN_URI = "/oauth/token";
  
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
    
    log.debug("{}",urlVariables);
    log.debug("{}",uri);
    
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    String url = getUrl(providerData.findValueForKey("base_url"), uri, pageable);
    
    ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
    resourceDetails.setClientId(providerData.findValueForKey("key"));
    resourceDetails.setClientSecret(providerData.findValueForKey("secret"));
    resourceDetails.setAccessTokenUri(getUrl(providerData.findValueForKey("base_url"), LAP_OAUTH_TOKEN_URI, null));
    DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
    ParameterizedTypeReference<PageWrapper<ModelOutputImpl>> responseType = new ParameterizedTypeReference<PageWrapper<ModelOutputImpl>>() {};
    
    PageWrapper<ModelOutputImpl> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, null, responseType, urlVariables).getBody();
    log.debug(pageWrapper.toString());
    List<ModelOutput> output;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      output = new LinkedList<ModelOutput>(pageWrapper.getContent());
    }
    else {
      output = new ArrayList<ModelOutput>();
    }
    
    return new PageImpl<ModelOutput>(output, pageable, pageWrapper.getPage().getTotalElements());
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
  public Page<ModelOutput> getModelOutputForCourse(ProviderOptions options, String tenant, String course, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", course);
    urlVariables.put("tenant", (StringUtils.isNotBlank(tenant)) ? tenant : "lap");
    
    return fetch(urlVariables, pageable, "/api/output/{tenant}/course/{id}?lastRunOnly=true");
  }

  @Override
  public Page<ModelOutput> getModelOutputForStudent(ProviderOptions options, String tenant, String student, Pageable pageable) throws ProviderException {
    Map<String, String> urlVariables = new HashMap<String, String>();
    urlVariables.put("id", student);
    urlVariables.put("tenant", (StringUtils.isNotBlank(tenant)) ? tenant : "lap");
    
    return fetch(urlVariables, pageable, "/api/outpu/{tenant}t/student/{id}");
  }
}
