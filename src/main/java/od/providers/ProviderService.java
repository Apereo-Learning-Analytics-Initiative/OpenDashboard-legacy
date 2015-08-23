/**
 * 
 */
package od.providers;

import java.util.Map;
import java.util.Set;

import od.providers.modeloutput.ModelOutputProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class ProviderService {
  @Autowired private Map<String, ModelOutputProvider> modelOutputProviders;
  
  public ModelOutputProvider getModelOutputProvider(String key) {
    return modelOutputProviders.get(key);
  }
}
