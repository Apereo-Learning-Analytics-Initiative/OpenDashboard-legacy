package od.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import lti.LaunchRequest;
import od.entrypoints.LTIEntryPointController;

@Component
@WebFilter(urlPatterns = "/login")
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return (path.startsWith("/lti") || path.startsWith("/login"));
    }
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {		

		// get token from a Cookie
		Cookie[] cookies = request.getCookies();
		Cookie sessionCookie = null;
		if(cookies !=null) {
			for (Cookie cookie : cookies) {
				if (("securityToken").equals(cookie.getName())) {
					sessionCookie = cookie;
					break;
				}
			}
		}

		// TODO: move the cookie validation into a private method
		if (sessionCookie == null || StringUtils.isEmpty(sessionCookie.getValue())) {
			throw new AuthenticationServiceException("Invalid Token");
		}

		String jwtToken = sessionCookie.getValue();
		//jwtTokenUtil.
		Map<String, Object> claims = jwtTokenUtil.getClaimsFromToken(jwtToken);	
		

		// **********************************
		String userSourcedId = null;
		if(claims.get("userSourcedId") == null) {
			userSourcedId = "ADMIN";
		}
		else {
			userSourcedId = claims.get("userSourcedId").toString();
		}
		
		String tenantId = null;
		if(claims.get("tenantId") == null) {
			tenantId = "ADMIN";
		}
		else {
			tenantId = claims.get("tenantId").toString();
		}
		
		LaunchRequest launchRequest = null;
		if(claims.get("launchRequest") == null) {
			launchRequest = null;
		}
		else {
			launchRequest = LaunchRequest.fromJSON(claims.get("launchRequest").toString());
		}
		
		String userEmail = null;
		if(launchRequest == null) 
		{
			userEmail = "ADMIN_NOEMAIL";
		}
		else {			
			userEmail = launchRequest.getLis_person_contact_email_primary();
		}

		// Once we get the token validate it.
		if (userSourcedId != null && SecurityContextHolder.getContext().getAuthentication() == null) {			
			// if token is valid configure Spring Security to manually set
			// authentication
			// if (jwtTokenUtil.validateToken(jwtToken, userDetails))
			if (true) {

				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				// authorities.add(GrantedAuthority);
								
				
				
				/*
			    // Get the role of this guy
			    String role;
			    if (LTIEntryPointController.hasInstructorRole(null, launchRequest.getRoles())) {
			      role = "ROLE_INSTRUCTOR";
			    } else if (LTIEntryPointController.hasLearnerRole(null, launchRequest.getRoles())) {
			      role = "ROLE_STUDENT";
			    } else {
			      throw new UnauthorizedUserException("Does not have the instructor or learner role");
			    }
			    */
			    List<String> authoritiesString = (List<String>) claims.get("Authorities");

				OpenDashboardAuthenticationToken oda = new OpenDashboardAuthenticationToken(launchRequest, jwtToken,
						tenantId, userSourcedId, userSourcedId,
						AuthorityUtils.commaSeparatedStringToAuthorityList(
								authoritiesString.stream()
                                .collect(Collectors.joining(","))),
						userEmail);

				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the
				// Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(oda);
			}
		}
		filterChain.doFilter(request, response);
	}
}