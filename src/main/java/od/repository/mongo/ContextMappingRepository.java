/**
 *
 */
package od.repository.mongo;

import od.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Profile("mongo")
public interface ContextMappingRepository extends MongoRepository<ContextMapping, String>, ContextMappingRepositoryInterface {
    @Override
    ContextMapping findByKeyAndContext(final String key, final String context);
}
