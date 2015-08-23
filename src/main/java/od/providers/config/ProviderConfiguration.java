/**
 * 
 */
package od.providers.config;

import java.util.LinkedList;

/**
 * @author ggilbert
 *
 */
public interface ProviderConfiguration {
  LinkedList<ProviderConfigurationOption> getOptions();
}
