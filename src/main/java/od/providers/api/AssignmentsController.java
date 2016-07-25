/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.providers.api;

import java.util.List;

import od.framework.model.ContextMapping;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.providers.assignment.AssignmentsProvider;
import od.repository.mongo.ContextMappingRepository;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.impl.AssignmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class AssignmentsController {
	
	private static final Logger log = LoggerFactory.getLogger(AssignmentsController.class);
	
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private ContextMappingRepository contextMappingRepository;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/tenants/{tenantId}/contexts/{contextMappingId}/assignments", method = RequestMethod.GET)
	public List<AssignmentImpl> assignment(@PathVariable("tenantId") final String tenantId,
      @PathVariable("contextMappingId") final String contextMappingId)
			throws Exception {

    log.debug("tenantId: {}", tenantId);
    log.debug("contextMappingId: {}", contextMappingId);

    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    ContextMapping contextMapping = contextMappingRepository.findOne(contextMappingId);
    AssignmentsProvider assignmentsProvider = providerService.getAssignmentsProvider(tenant);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ASSIGNMENT);
    
    return assignmentsProvider.getAssignments(providerData,contextMapping.getContext());
	}
}
