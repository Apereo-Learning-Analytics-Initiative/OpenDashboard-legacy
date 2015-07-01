/**
 * 
 */
package od.roster;

import java.util.Set;

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
public class RosterController {
	
	private static final Logger log = LoggerFactory.getLogger(RosterController.class);
	
	@Autowired private RosterProvider rosterProvider;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/roster", method = RequestMethod.POST)
	public Set<Member> roster(@RequestBody ProviderOptions options)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug(options.toString());
		}
		
		return rosterProvider.getRoster(options);
	}
}
