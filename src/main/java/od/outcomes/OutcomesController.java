/**
 * 
 */
package od.outcomes;

import java.util.List;

import od.providers.ProviderOptions;
import od.repository.ContextMappingRepositoryInterface;

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
public class OutcomesController {
	private static final Logger log = LoggerFactory.getLogger(OutcomesController.class);
	
	@Autowired private OutcomesProvider outcomesProvider;
	@Autowired private ContextMappingRepositoryInterface contextMappingRepository;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/outcomes", method = RequestMethod.POST)
	public List<LineItem> outcomes(@RequestBody ProviderOptions providerOptions)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug(providerOptions.toString());
		}
		return outcomesProvider.getOutcomesForCourse(providerOptions);
	}
}
