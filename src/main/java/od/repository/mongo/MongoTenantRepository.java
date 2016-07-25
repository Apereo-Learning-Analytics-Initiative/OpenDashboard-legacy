/**
 * 
 */
package od.repository.mongo;

import od.framework.model.Tenant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author ggilbert
 *
 */
public interface MongoTenantRepository extends MongoRepository<Tenant, String> {
  @Query(value = "{ 'consumers.oauthConsumerKey' : ?0 }")
  Tenant findByConsumersOauthConsumerKey(String oauthConsumerKey);

}
