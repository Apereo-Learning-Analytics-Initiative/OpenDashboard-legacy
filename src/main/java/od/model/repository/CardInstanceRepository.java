/**
 * 
 */
package od.model.repository;

import java.util.List;

import od.model.CardInstance;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Repository
public interface CardInstanceRepository extends MongoRepository<CardInstance, String> {
	List<CardInstance> findByContextOrderBySequenceAsc(final String context);
}
