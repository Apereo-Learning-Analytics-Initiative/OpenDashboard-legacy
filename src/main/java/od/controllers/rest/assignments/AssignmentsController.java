/**
 * 
 */
package od.controllers.rest.assignments;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import od.model.assignments.Assignment;
import od.model.roster.Member;
import od.providers.assignments.AssignmentsProvider;
import od.providers.roster.RosterProvider;

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
public class AssignmentsController {
	
	private static final Logger log = LoggerFactory.getLogger(AssignmentsController.class);
	
	@Autowired private AssignmentsProvider assignmentsProvider;
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/{contextMappingId}/db/{dashboardId}/card/{cardId}/assignments", method = RequestMethod.POST)
	public Set<Assignment> assignment(
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
		
		String sakaiSessionId = null;
		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("JSESSIONID"))
			{
				sakaiSessionId = cookie.getValue();
			}
		}
		
		String sakaiServer = options.get("ext_sakai_server") != null ?options.get("ext_sakai_server") : "http://localhost:8080";
		
		return assignmentsProvider.getAssignments(sakaiServer + "/direct/assignment/site/" + options.get("courseId") + ".xml", sakaiSessionId);
	}
}
