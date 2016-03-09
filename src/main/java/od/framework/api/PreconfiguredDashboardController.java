/**
 * 
 */
package od.framework.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import od.framework.model.Dashboard;
import od.repository.PreconfiguredDashboardRepositoryInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class PreconfiguredDashboardController {
  
  @Autowired private PreconfiguredDashboardRepositoryInterface preconfiguredDashboardRepositoryInterface;
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure", method = RequestMethod.POST, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public Dashboard create(@RequestBody Dashboard dashboard) {
    return preconfiguredDashboardRepositoryInterface.save(dashboard);
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure/{id}", method = RequestMethod.PUT, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public Dashboard update(@RequestBody Dashboard dashboard, @PathVariable("id") final String id) {
    return preconfiguredDashboardRepositoryInterface.save(dashboard);
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public List<Dashboard> get() {
    return preconfiguredDashboardRepositoryInterface.findAll();
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure/{id}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public Dashboard get(@PathVariable("id") final String id) {
    return preconfiguredDashboardRepositoryInterface.findOne(id);
  }

  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure/{id}", method = RequestMethod.DELETE, 
      produces = "application/json;charset=utf-8")
  //@ResponseStatus(HttpS)
  public void delete(@PathVariable("id") final String id) {
    preconfiguredDashboardRepositoryInterface.delete(id);
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/preconfigure/checktitle/{title}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  //@ResponseStatus(HttpS)
  public Map<String, Boolean> checkTitle(@PathVariable("title") final String title) {
    Dashboard pcdWithTitleMatch = preconfiguredDashboardRepositoryInterface.findByTitle(title);
    if (pcdWithTitleMatch != null) {
      return Collections.singletonMap("exists", true);
    }
    else {
      return Collections.singletonMap("exists", false);
    }
  }
  
}
