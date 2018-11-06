/**
 * 
 */
package od.providers.api;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import unicon.matthews.oneroster.User;


/**
 * @author ggilbert
 *
 */
@RestController
public class UserController {
  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT"})
  @RequestMapping(value = "/api/tenants/{tenantId}/users/{userId}", method = RequestMethod.GET)
  public User getEnrollmentsForUser(@PathVariable("tenantId") final String tenantId,
      @PathVariable("userId") final String userId)
      throws Exception {
    log.debug("tenantId: {}", tenantId);
    log.debug("userId: {}", userId);

    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    UserProvider userProvider = providerService.getUserProvider(tenant);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.USER);
    
    return userProvider.getUserBySourcedId(providerData, userId);
  }

}
