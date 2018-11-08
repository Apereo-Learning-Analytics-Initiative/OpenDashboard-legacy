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

import java.util.Set;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.providers.enrollment.EnrollmentProvider;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import unicon.matthews.oneroster.Enrollment;


/**
 * @author ggilbert
 *
 */
@RestController
public class EnrollmentController {
	
	private static final Logger log = LoggerFactory.getLogger(EnrollmentController.class);
	
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT"})
	@RequestMapping(value = "/api/tenants/{tenantId}/users/{userId}/enrollments", method = RequestMethod.GET)
	public Set<Enrollment> getEnrollmentsForUser(@PathVariable("tenantId") final String tenantId,
      @PathVariable("userId") final String userId)
			throws Exception {
	  log.debug("tenantId: {}", tenantId);
	  log.debug("userId: {}", userId);

	  Tenant tenant = mongoTenantRepository.findOne(tenantId);
    EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
    
		return enrollmentProvider.getEnrollmentsForUser(providerData, userId, true);
	}
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_ADMIN"})
  @RequestMapping(value = "/api/tenants/{tenantId}/classes/{classId}/enrollments", method = RequestMethod.GET)
  public Set<Enrollment> getEnrollmentsForClass(@PathVariable("tenantId") final String tenantId,
      @PathVariable("classId") final String classId)
      throws Exception {
    log.debug("tenantId: {}", tenantId);
    log.debug("classId: {}", classId);

    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
    
    return enrollmentProvider.getEnrollmentsForClass(providerData, classId, true);
  }

}
