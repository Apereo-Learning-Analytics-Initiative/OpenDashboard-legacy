/**
 * 
 */
package od.providers.matthews;

import java.util.LinkedList;

import od.providers.Provider;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;

/**
 * @author ggilbert
 *
 */
public abstract class MatthewsProvider implements Provider {
  protected ProviderConfiguration providerConfiguration;
  
  public ProviderConfiguration getDefaultMatthewsConfiguration() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "Matthews Base URL", "LABEL_MATTHEWS_BASE_URL", false);
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
