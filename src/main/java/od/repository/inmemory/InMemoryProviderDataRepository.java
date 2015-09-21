/**
 * 
 */
package od.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import od.providers.ProviderData;
import od.repository.ProviderDataRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Profile("inmemory")
@Component
public class InMemoryProviderDataRepository implements ProviderDataRepositoryInterface {

  private static Map<String, ProviderData> store = new HashMap<String, ProviderData>();
  
  @Override
  public ProviderData save(ProviderData providerData) {
    return store.put(providerData.getProviderKey(), providerData);
  }

  @Override
  public void delete(ProviderData providerData) {
     store.remove(providerData.getProviderKey());
  }

  @Override
  public ProviderData findByProviderKey(String key) {
    return store.get(key);
  }

  @Override
  public List<ProviderData> findByProviderType(String type) {
    if (!store.isEmpty()) {
      List<ProviderData> pds = new ArrayList<ProviderData>();
      for (ProviderData pd : store.values()) {
        if (type.equals(pd.getProviderType())) {
          pds.add(pd);
        }
      }
      return pds;
    }
    return null;
  }

}
