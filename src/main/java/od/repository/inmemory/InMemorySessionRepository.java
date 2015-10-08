/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.repository.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import od.framework.model.Session;
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
