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

import java.util.LinkedList;

import od.providers.BaseProvider;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;

/**
 * @author ggilbert
 *
 */
public abstract class LearningLockerProvider extends BaseProvider {
  
  protected static final String LL_OAUTH_TOKEN_URI = "/oauth/token";
  
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

}
