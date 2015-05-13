/**
 * 
 */
package od.repository.inmemory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import od.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;

/**
 * @author ggilbert
 *
 */
@Profile("inmemory")
@Component
public class InMemoryContextMappingRepository implements
    ContextMappingRepositoryInterface {
  
  private static Map<String, ContextMapping> store = new HashMap<String, ContextMapping>();

  @Override
  public ContextMapping findOne(String key) {
    return store.get(key);
  }

  @Override
  public ContextMapping findByKeyAndContext(String key, String context) {
    String catKey = key + ":" + context;
    return findOne(catKey);
  }

  @Override
  public ContextMapping save(ContextMapping contextMapping) {
    String context = contextMapping.getContext();
    String key = contextMapping.getKey();
    
    String catKey = key + ":" + context;
    contextMapping.setId(catKey);
    
    store.put(catKey, contextMapping);
    
    return contextMapping;
  }

}
