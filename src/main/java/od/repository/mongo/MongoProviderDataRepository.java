/**
 * 
 */
package od.repository.mongo;

import java.util.List;

import od.providers.ProviderData;
import od.repository.ProviderDataRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
@Profile("mongo")
public interface MongoProviderDataRepository extends ProviderDataRepositoryInterface, MongoRepository<ProviderData, String> {
  @Override ProviderData findByProviderKey(final String key);
  @Override List<ProviderData> findByProviderType(final String type);
}
