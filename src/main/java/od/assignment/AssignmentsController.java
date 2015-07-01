/**
 * 
 */
package od.assignment;

import java.util.List;

import od.providers.ProviderOptions;

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
	
	@Autowired private AssignmentsProvider assignmentsProvider;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/assignments", method = RequestMethod.POST)
	public List<Assignment> assignment(@RequestBody ProviderOptions options)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("options " + options);
		}
		
		return assignmentsProvider.getAssignments(options);
	}
}
