/**
 * 
 */
package od.providers;

import od.providers.config.ProviderConfiguration;

/**
 * @author ggilbert
 *
 */
public interface Provider {
  String getKey();
  String getName();
  String getDesc();
  ProviderConfiguration getProviderConfiguration();
}
