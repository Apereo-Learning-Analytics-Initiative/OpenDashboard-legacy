/**
 * 
 */
package od.model.repository;

import od.model.Session;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Repository
public interface SessionRepository extends MongoRepository<Session, String> {

}
