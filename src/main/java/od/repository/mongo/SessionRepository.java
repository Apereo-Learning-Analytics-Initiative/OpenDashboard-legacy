/**
 *
 */
package od.repository.mongo;

import od.framework.model.Session;
import od.repository.SessionRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
@Profile("mongo")
public interface SessionRepository extends MongoRepository<Session, String>, SessionRepositoryInterface {

}
