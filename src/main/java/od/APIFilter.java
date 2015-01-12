/**
 * 
 */
package od;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import od.model.Session;
import od.model.repository.SessionRepository;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author ggilbert
 *
 */
@Component
public class APIFilter extends OncePerRequestFilter {
	
	private Logger log = Logger.getLogger(APIFilter.class);
	
	@Autowired private SessionRepository sessionRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String odAuthHeader = request.getHeader("X-OD-AUTH");
		
		if (StringUtils.isNotBlank(odAuthHeader)) {
			if (log.isDebugEnabled()) {
				log.debug("X-OD-AUTH: "+odAuthHeader);
			}
			
			Session session = sessionRepository.findOne(odAuthHeader);
			if (session != null) {
				// TODO expired
				chain.doFilter(request, response);
			}
			else {
				 response.setHeader("WWW-Authenticate", "X-OD-AUTH realm=\"OpenDashboard\"");
				 response.sendError(401, "Invalid authentication token");
			}
		}
		else {
			 response.setHeader("WWW-Authenticate", "X-OD-AUTH realm=\"OpenDashboard\"");
			 response.sendError(401, "Missing authentication token");
		}
		
	}
}
