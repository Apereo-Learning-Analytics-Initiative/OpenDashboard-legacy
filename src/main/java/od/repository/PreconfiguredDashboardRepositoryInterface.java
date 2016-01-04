/**
 * 
 */
package od.repository;

import java.util.List;

import od.framework.model.Dashboard;

/**
 * @author ggilbert
 *
 */
public interface PreconfiguredDashboardRepositoryInterface {
  Dashboard findOne(final String id);
  Dashboard findByTitle(final String title);
  List<Dashboard> findAll();
  Dashboard save(final Dashboard dashboard);
  void delete(final String id);
}
