/**
 * 
 */
package od.providers.api;

import java.util.Set;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.providers.lineitem.LineItemProvider;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import unicon.matthews.oneroster.LineItem;


/**
 * @author ggilbert
 *
 */
@RestController
public class LineItemController {
  private static final Logger log = LoggerFactory.getLogger(LineItemController.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_ADMIN"})
  @RequestMapping(value = "/api/tenants/{tenantId}/classes/{classId}/lineitems", method = RequestMethod.GET)
  public Set<LineItem> getEnrollmentsForClass(@PathVariable("tenantId") final String tenantId,
      @PathVariable("classId") final String classId)
      throws Exception {
    log.debug("tenantId: {}", tenantId);
    log.debug("classId: {}", classId);

    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    LineItemProvider lineItemProvider = providerService.getLineItemProvider(tenant);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.LINEITEM);
    return lineItemProvider.getLineItemsForClass(providerData, classId);
  }

}
