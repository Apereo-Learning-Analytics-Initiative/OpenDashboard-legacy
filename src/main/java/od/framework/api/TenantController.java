/**
 * 
 */
package od.framework.api;

import java.util.ArrayList;
import java.util.List;

import od.framework.model.Setting;
import od.framework.model.Tenant;

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
public class TenantController {
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/tenant", method = RequestMethod.POST, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public Tenant create(@RequestBody Tenant tenant) {
    //return tenantRepositoryInterface.save(tenant);
    return null;
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/tenant", method = RequestMethod.PUT, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public List<Tenant> update(@RequestBody List<Tenant> tenants) {
      List<Tenant> list = new ArrayList<>();
      Tenant s;
      for (Tenant tenant : tenants){
         //s = tenantRepositoryInterface.save(tenant);
         //list.add(s);
      }
      return list;
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/tenant", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public List<Tenant> getAll() {
    //return tenantRepositoryInterface.findAll();
    return null;
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/tenant/{id}", method = RequestMethod.DELETE, 
      produces = "application/json;charset=utf-8")
  public void delete(@PathVariable("id") final String id) {
      //tenantRepositoryInterface.delete(id);
  }

}
