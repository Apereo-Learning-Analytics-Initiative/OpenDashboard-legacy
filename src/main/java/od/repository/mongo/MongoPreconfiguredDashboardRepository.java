/**
 * 
 */
package od.repository.mongo;

import od.framework.model.Dashboard;
import od.repository.PreconfiguredDashboardRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ggilbert
 *
 */
public interface MongoPreconfiguredDashboardRepository extends PreconfiguredDashboardRepositoryInterface, MongoRepository<Dashboard, String> {

}
