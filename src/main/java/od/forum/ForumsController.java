/**
 * 
 */
package od.forum;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ForumsController {
	
	private static final Logger log = LoggerFactory.getLogger(ForumsController.class);
	
	@Autowired private ForumsProvider forumsProvider;
	
	@Value("${sakai.host}")
    private String sakaiHost;

	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/{contextMappingId}/db/{dashboardId}/card/{cardId}/forums", method = RequestMethod.POST)
	public Set<Forum> forums(
			HttpServletRequest request, 
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
		
		return forumsProvider.getForums(options);
	}
}
