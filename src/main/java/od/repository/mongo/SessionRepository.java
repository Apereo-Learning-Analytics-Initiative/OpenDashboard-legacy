/**
 *
 */
package od.repository.mongo;

import od.model.Session;
import od.repository.SessionRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Profile("mongo")
public interface SessionRepository extends MongoRepository<Session, String>, SessionRepositoryInterface {

}
