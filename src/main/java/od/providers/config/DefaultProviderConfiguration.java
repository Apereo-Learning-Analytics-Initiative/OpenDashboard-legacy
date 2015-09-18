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
