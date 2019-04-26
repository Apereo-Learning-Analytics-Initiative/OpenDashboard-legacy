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
package od.providers.learninglocker;

import java.util.Arrays;
import java.util.LinkedList;

import od.providers.BaseProvider;
import od.providers.ProviderData;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
public abstract class LearningLockerProvider extends BaseProvider {
  
  protected static final String LL_OAUTH_TOKEN_URI = "/oauth/token";
  protected static final String STAFF_URI = "/api/jisc/v1.2.5/staff";
  protected static final String STAFF_MODULE_INSTANCE_URI = "/api/jisc/v1.2.5/staffmoduleinstance";
  protected static final String MODULE_INSTANCE_URI = "/api/jisc/v1.2.5/moduleinstance";
  protected static final String MODULE_VLE_MAP_URI = "/api/jisc/v1.2.5/modulevlemap";
  protected static final String STUDENT_MODULE_INSTANCE_URI = "/api/jisc/v1.2.5/studentmoduleinstance";
  protected static final String STUDENT_URI = "/api/jisc/v1.2.5/student";

  
  @Value("${ll.use.oauth:false}")
  protected boolean OAUTH;
  
  @Value("${ll.use.demo:false}")
  protected boolean DEMO = false;

  protected ProviderConfiguration providerConfiguration;
  
  public ProviderConfiguration getDefaultLearningLockerConfiguration() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "Learning Locker Base URL", "LABEL_LEARNINGLOCKER_BASE_URL", false);
    options.add(key);
    options.add(secret);
    options.add(baseUrl);

    return new DefaultProviderConfiguration(options);
  }
  
  @Override
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }
  
  protected RestTemplate getRestTemplate(ProviderData providerData) {
    RestTemplate restTemplate = null;
    
    if (OAUTH) {
      
      String baseUrl = buildUrl(providerData.findValueForKey("base_url"), LL_OAUTH_TOKEN_URI);

      ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
      resourceDetails.setClientId(providerData.findValueForKey("key"));
      resourceDetails.setClientSecret(providerData.findValueForKey("secret"));
      resourceDetails.setAccessTokenUri(baseUrl);
      DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

      restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
    }
    else {
      restTemplate = new RestTemplate();
      
    }
    
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
    
    return restTemplate;
  }

  

}
