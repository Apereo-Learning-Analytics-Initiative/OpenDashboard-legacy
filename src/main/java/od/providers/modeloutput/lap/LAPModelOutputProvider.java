/**
 * 
 */
package od.providers.modeloutput.lap;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.KeyValueProviderConfigurationOption;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.modeloutput.ModelOutputProvider;

import org.apereo.lai.ModelOutput;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("modeloutput_lap")
public class LAPModelOutputProvider implements ModelOutputProvider {
  
  private static final String KEY = "modeloutput_lap";
  private static final String NAME = "Apereo Learning Analytics Processor";
  private ProviderConfiguration providerConfiguration;
  
  @PostConstruct
  public void init() {
    ProviderConfigurationOption key = new KeyValueProviderConfigurationOption("OAuth Consumer Key", null, ProviderConfigurationOption.TEXT_TYPE, true);
    ProviderConfigurationOption secret = new KeyValueProviderConfigurationOption("Secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true);
    ProviderConfigurationOption baseUrl = new KeyValueProviderConfigurationOption("Base URL", null, ProviderConfigurationOption.URL_TYPE, true);
    
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    options.add(key);
    options.add(secret);
    options.add(baseUrl);
    
    providerConfiguration = new DefaultProviderConfiguration(options);
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

  @Override
  public List<ModelOutput> getModelOutputForCourse(ProviderOptions options, String course, Pageable page) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ModelOutput> getModelOutputForStudent(ProviderOptions options, String student, Pageable page) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

}
