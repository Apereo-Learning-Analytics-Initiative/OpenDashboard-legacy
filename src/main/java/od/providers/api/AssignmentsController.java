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

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.assignment.AssignmentsProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.impl.AssignmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/assignments", method = RequestMethod.POST)
	public List<AssignmentImpl> assignment(@RequestBody ProviderOptions options)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("options " + options);
		}
		
    AssignmentsProvider assignmentsProvider = providerService.getAssignmentsProvider(mongoTenantRepository.findOne(options.getTenantId()));
		
		return assignmentsProvider.getAssignments(options);
	}
}
