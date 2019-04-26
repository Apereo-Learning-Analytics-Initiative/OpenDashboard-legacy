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

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.providers.course.CourseProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Course;
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
public class CourseController {
  private static final Logger log = LoggerFactory.getLogger(CourseController.class);
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_ADMIN"})
  @RequestMapping(value = "/api/tenants/{tenantId}/user/{userId}/memberships", method = RequestMethod.GET)
  public List<Course> contexts(@PathVariable("tenantId") final String tenantId,
      @PathVariable("userId") final String userId)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("tenantId: {}", tenantId);
      log.debug("userId: {}", userId);
    }
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    ProviderData providerData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.COURSE);
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);

    return courseProvider.getContexts(providerData,userId);
  }
}
