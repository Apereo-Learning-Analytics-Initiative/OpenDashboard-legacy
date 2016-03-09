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
package od.repository.mongo;

import java.util.List;

import od.providers.ProviderData;
import od.repository.ProviderDataRepositoryInterface;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
public interface MongoProviderDataRepository extends ProviderDataRepositoryInterface, MongoRepository<ProviderData, String> {
  @Override ProviderData findByProviderKey(final String key);
  @Override List<ProviderData> findByProviderType(final String type);
}
