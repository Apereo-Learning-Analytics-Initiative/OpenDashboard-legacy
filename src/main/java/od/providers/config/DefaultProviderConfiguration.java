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
package od.providers.config;

import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ggilbert
 *
 */
public class DefaultProviderConfiguration implements ProviderConfiguration {
  
  private LinkedList<ProviderConfigurationOption> options;
  
  public DefaultProviderConfiguration(LinkedList<ProviderConfigurationOption> options) {
    super();
    this.options = options;
  }

  @Override
  public LinkedList<ProviderConfigurationOption> getOptions() {
    return options;
  }

  @Override
  public ProviderConfigurationOption getByKey(String key) {
    
    if (StringUtils.isBlank(key)) {
      throw new IllegalArgumentException("key cannot be null or empty");
    }
    
    if (this.options != null && !this.options.isEmpty()) {
      for (ProviderConfigurationOption pco : this.options) {
        if (key.equals(pco.getKey())) {
          return pco;
        }
      }
    }
    return null;
  }

}
