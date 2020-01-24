package od.added;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;
import od.auth.JwtTokenUtil;
import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.PulseClassDetail;
import od.framework.model.PulseDateEventCount;
import od.framework.model.PulseDetail;
import od.framework.model.PulseStudentDetail;
import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.enrollment.EnrollmentProvider;
import od.providers.events.EventProvider;
import od.providers.lineitem.LineItemProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;
import od.repository.mongo.PulseCacheRepository;
import od.utils.PulseUtility;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.ModelOutput;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.emory.mathcs.backport.java.util.Arrays;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

@RestController
public class AuthController {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	@Value("${opendashboard.uxbaseurl}")
	private String uxBaseUrl;
	
	@Value("${od.admin.user:admin}")
	private String adminUsername;
	  
	@Value("${od.admin.password:admin}")
	private String adminPassword;

	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String login(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {// ,
		// @RequestParam("user") String user, @RequestParam("pass") String pass)

		// get token from a Cookie		
		//Iterator<String> itr = request.getHeaderNames()
		String auth = request.getHeader("Authorization");
		auth = auth.replaceAll("Basic ", "");		
		String decoded = new String(Base64.getDecoder().decode(auth.getBytes()));
		String username = decoded.substring(0,decoded.indexOf(':'));
		String password = decoded.substring(decoded.indexOf(':')+1,decoded.length());

		
		if(!(username.equals(adminUsername) && password.equals(adminPassword)))  
		{ 
			//remove existing cookie and start over :)
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
		    Cookie cookie = new Cookie("securityToken", "");
		    cookie.setMaxAge(0);
		    response.addCookie(cookie);
			return "401";
		}	
		
		
		Map<String, Object> claims = new HashMap<>();
		Set<String> authoritiesSet = new HashSet<>();
		
		Cookie[] cookies = request.getCookies();
		Cookie sessionCookie = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (("securityToken").equals(cookie.getName())) {
					sessionCookie = cookie;
					break;
				}
			}
		}

		if (sessionCookie != null && !StringUtils.isEmpty(sessionCookie.getValue())) {
			String jwtToken = sessionCookie.getValue();
			claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
			List<String> authoritiesList = (List<String>) claims.get("Authorities");
			authoritiesSet = new HashSet<String>(authoritiesList);
		}

		authoritiesSet.add("ROLE_ADMIN");
		claims.put("Authorities", authoritiesSet);
		final String jwtToken = jwtTokenUtil.generateToken(claims);

		Cookie cookie = new Cookie("securityToken", jwtToken);

		response.addCookie(cookie);
		return "OK";
	}
}

