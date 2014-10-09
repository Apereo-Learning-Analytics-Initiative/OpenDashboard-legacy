/**
 * 
 */
package od.model.repository;

import od.model.Card;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
public interface CardRepository extends MongoRepository<Card, String> {

}
