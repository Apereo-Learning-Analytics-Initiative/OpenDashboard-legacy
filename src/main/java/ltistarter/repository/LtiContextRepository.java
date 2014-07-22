/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.repository;

import ltistarter.model.LtiContextEntity;
import ltistarter.model.LtiKeyEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * NOTE: use of this interface magic makes all subclass-based (CGLIB) proxies fail
 */
@Transactional
public interface LtiContextRepository extends PagingAndSortingRepository<LtiContextEntity, Long> {
    /* Add custom crud methods here
     * If you need a custom implementation of the methods then see docs for steps to add it
     * http://docs.spring.io/spring-data/data-commons/docs/current/reference/html/repositories.html
     * Can also write a custom query like so:
     * @Query("SELECT u FROM User u WHERE u.alias IS NOT NULL")
     * List<User> findAliased();
     * OR:
     * @Query("SELECT u FROM User u WHERE u.alias = ?1")
     * List<User> findWithAlias(String alias);
     */

    /**
     * @param key the unique key
     * @return the LtiContextEntity OR null if there is no entity matching this key
     */
    LtiKeyEntity findByContextKey(String key);

    /**
     * @param key the unique key
     * @return the number of keys removed (0 or 1)
     */
    int deleteByContextKey(String key);
}
