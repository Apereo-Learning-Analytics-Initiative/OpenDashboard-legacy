/**
 *
 */
package od.repository.mongo;

import od.framework.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
@Profile("mongo")
public interface ContextMappingRepository extends MongoRepository<ContextMapping, String>, ContextMappingRepositoryInterface {
    @Override
    ContextMapping findByKeyAndContext(final String key, final String context);
}
