/**
 * 
 */
package od.providers.api;

import java.util.List;

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.outcomes.OutcomesProvider;

import org.apereo.lai.impl.LineItemImpl;
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
	
  @Autowired private ProviderService providerService;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/outcomes", method = RequestMethod.POST)
	public List<LineItemImpl> outcomes(@RequestBody ProviderOptions providerOptions)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug(providerOptions.toString());
		}
    OutcomesProvider outcomesProvider = providerService.getOutcomesProvider(OutcomesProvider.DEFAULT);

		return outcomesProvider.getOutcomesForCourse(providerOptions);
	}
}
