/**
 * 
 */
package od.providers.config;

import java.util.LinkedList;

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

}
