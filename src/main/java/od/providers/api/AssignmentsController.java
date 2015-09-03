/**
 * 
 */
package od.providers.api;

import java.util.List;

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.assignment.AssignmentsProvider;

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
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/assignments", method = RequestMethod.POST)
	public List<AssignmentImpl> assignment(@RequestBody ProviderOptions options)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("options " + options);
		}
		
    AssignmentsProvider assignmentsProvider = providerService.getAssignmentsProvider(AssignmentsProvider.DEFAULT);
		
		return assignmentsProvider.getAssignments(options);
	}
}
