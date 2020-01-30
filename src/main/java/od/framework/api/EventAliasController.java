/**
 * 
 */
package od.framework.api;

import java.util.ArrayList;
import java.util.List;

import od.framework.model.EventAlias;
import od.framework.model.Tenant;
import od.repository.mongo.MongoEventAliasRepository;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author scody
 *
 */
@RestController
public class EventAliasController {
  private static final Logger log = LoggerFactory.getLogger(EventAliasController.class);
  @Autowired private MongoEventAliasRepository mongoEventAliasRepository;
  
  @Secured("ROLE_ADMIN")
  @CrossOrigin 
  @RequestMapping(value = "/api/eventAlias/tenantId/{tenantId}", method = RequestMethod.POST, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public EventAlias create(@RequestBody EventAlias eventAlias, @PathVariable("tenantId") final String tenantId) {
    log.debug("{}",eventAlias);
    eventAlias.setTenantId(tenantId);	
    return mongoEventAliasRepository.save(eventAlias);
  }
  
  @Secured("ROLE_ADMIN")
  @CrossOrigin
  @RequestMapping(value = "/api/eventAlias/tenantId/{tenantId}", method = RequestMethod.PUT, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public EventAlias update(@RequestBody EventAlias eventAlias, @PathVariable("tenantId") final String tenantId) {
	eventAlias.setTenantId(tenantId);	  
    return mongoEventAliasRepository.save(eventAlias);
  }
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR"})
  @RequestMapping(value = "/api/eventAlias/tenantId/{tenantId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public List<EventAlias> getOne(@PathVariable("tenantId") final String tenantId) {
    return mongoEventAliasRepository.findByTenantId(tenantId);
  }
  
  @Secured("ROLE_ADMIN")
  @RequestMapping(value = "/api/eventAlias/{id}", method = RequestMethod.DELETE, 
      produces = "application/json;charset=utf-8")
  public String delete(@PathVariable("id") final String id) {
    mongoEventAliasRepository.delete(id);
    return "{}";
  }
}
