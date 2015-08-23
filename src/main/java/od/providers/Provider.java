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
  ProviderConfiguration getProviderConfiguration();
}
