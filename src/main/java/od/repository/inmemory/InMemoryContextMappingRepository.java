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

import od.framework.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
