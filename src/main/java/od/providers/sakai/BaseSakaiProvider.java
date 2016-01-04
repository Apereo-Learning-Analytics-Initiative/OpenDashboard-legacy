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
package od.providers.sakai;

import java.util.LinkedList;

import od.providers.BaseProvider;
import od.providers.ProviderData;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
@Component
public abstract class BaseSakaiProvider extends BaseProvider {

  protected RestTemplate restTemplate = new RestTemplate();

  protected ProviderConfiguration getDefaultSakaiProviderConfiguration() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "User Key", "LABEL_USER_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "Sakai Base URL", "LABEL_SAKAI_BASE_URL", false);
    options.add(key);
    options.add(secret);
    options.add(baseUrl);

    return new DefaultProviderConfiguration(options);
  }

  protected String getSakaiSession(ProviderData providerData) {
    
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("_username", providerData.findValueForKey("key"));
    parameters.add("_password", providerData.findValueForKey("secret"));

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(
        parameters, null);
    
    String url = fullUrl(providerData, "/direct/session");

    return restTemplate.postForObject(url, entity, String.class);
  }
  
  protected String fullUrl(ProviderData providerData, String uri) {
    String sakai_host = providerData.findValueForKey("base_url");
    String url = null;
    if (sakai_host.endsWith("/")) {
      url = StringUtils.stripEnd(sakai_host, "/").concat(uri);
    }
    else {
      url = sakai_host.concat(uri);
    }
    
    return url;
  }

}
