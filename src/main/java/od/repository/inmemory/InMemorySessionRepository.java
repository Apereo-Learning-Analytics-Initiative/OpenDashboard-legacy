/**
 * 
 */
package od.repository.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import od.model.Session;
import od.repository.SessionRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Profile("inmemory")
@Component
public class InMemorySessionRepository implements SessionRepositoryInterface {
  
  private static Map<String, Session> store = new HashMap<String, Session>();

  @Override
  public Session findOne(String key) {
    return store.get(key);
  }

  @Override
  public Session save(Session session) {
    
    if (StringUtils.isBlank(session.getId())) {
      session.setId(UUID.randomUUID().toString());
    }
    
    store.put(session.getId(), session);
    
    return session;
  }

}
