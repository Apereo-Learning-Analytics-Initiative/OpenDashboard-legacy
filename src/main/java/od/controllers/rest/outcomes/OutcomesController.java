/**
 * 
 */
package od.controllers.rest.outcomes;

import java.util.Map;
import java.util.Set;

import od.model.ContextMapping;
import od.model.outcomes.LineItem;
import od.providers.outcomes.OutcomesProvider;
import od.repository.ContextMappingRepositoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
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
	@RequestMapping(value = "/api/{contextMappingId}/db/{dashboardId}/card/{cardId}/outcomes", method = RequestMethod.POST)
	public Set<LineItem> outcomes(
			@PathVariable("contextMappingId") String contextMappingId,
			@PathVariable("dashboardId") String dashboardId,
			@PathVariable("cardId") String cardId,
			@RequestBody Map<String, String> options)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("contextMappingId " + contextMappingId);
			log.debug("dashboardId " + dashboardId);
			log.debug("cardId " + cardId);
			log.debug("options " + options);
		}
		ContextMapping contextMapping = contextMappingRepository.findOne(contextMappingId);
		return outcomesProvider.getOutcomesForCourse(options, contextMapping.getContext());
	}
}
