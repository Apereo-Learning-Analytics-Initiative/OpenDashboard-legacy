/**
 * 
 */
package od.model.repository;

import od.model.ContextMapping;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Repository
public interface ContextMappingRepository extends MongoRepository<ContextMapping, String> {
	ContextMapping findByKeyAndContext(final String key, final String context);
}
