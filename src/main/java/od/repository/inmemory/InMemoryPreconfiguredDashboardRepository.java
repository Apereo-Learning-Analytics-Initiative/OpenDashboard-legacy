/**
 * 
 */
package od.repository.inmemory;

import java.util.List;

import od.framework.model.Dashboard;
import od.repository.PreconfiguredDashboardRepositoryInterface;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Profile("inmemory")
@Component
public class InMemoryPreconfiguredDashboardRepository implements PreconfiguredDashboardRepositoryInterface {

  @Override
  public Dashboard findOne(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Dashboard> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Dashboard save(Dashboard dashboard) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Dashboard findByTitle(String title) {
    // TODO Auto-generated method stub
    return null;
  }

}
