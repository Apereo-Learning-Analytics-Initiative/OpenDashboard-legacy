/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 *
 */
package od.entrypoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;
import od.auth.JwtTokenUtil;
import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.Tenant;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.ContextMappingRepository;
import od.repository.mongo.MongoTenantRepository;
import od.utils.PulseUtility;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import od.auth.OpenDashboardAuthenticationToken;

/**
 * @author ggilbert
 *
 */
@Controller
public class LTIEntryPointController {
  private static final Logger logger = LoggerFactory.getLogger(LTIEntryPointController.class);
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private ContextMappingRepository contextMappingRepository;
  @Autowired private ProviderService providerService;
  
  @Autowired private JwtTokenUtil jwtTokenUtil;
  
  @Autowired
  //@Qualifier(value="LTIAuthenticationManager")
  private AuthenticationManager authenticationManager;
  
  @Value("${opendashboard.baseurl}")
  private String baseUrl;

  @Value("${opendashboard.uxbaseurl}")
  private String uxBaseUrl;
  
  @Value("${opendashboard.basedomain}")
  private String baseDomain;
  
  @RequestMapping(value = { "/lti", "/lti/" }, method = RequestMethod.POST)
  public RedirectView lti(HttpServletRequest request, HttpServletResponse response) throws ProviderException, ProviderDataConfigurationException {
    LaunchRequest launchRequest = new LaunchRequest(request.getParameterMap());
    
    logger.debug("{}",launchRequest);
    
    String consumerKey = launchRequest.getOauth_consumer_key();
    //String contextId = launchRequest.getContext_id();
    
    Tenant tenant = mongoTenantRepository.findByConsumersOauthConsumerKey(consumerKey);

    // Create a token using spring provided class : LTIAuthenticationToken
    String role;
    if (LTIEntryPointController.hasInstructorRole(null, launchRequest.getRoles())) {
      role = "ROLE_INSTRUCTOR";
    } else if (LTIEntryPointController.hasLearnerRole(null, launchRequest.getRoles())) {
      role = "ROLE_STUDENT";
    } else {
      throw new UnauthorizedUserException("Does not have the instructor or learner role");
    }
    
    // Find user mapping
    UserProvider userProvider = providerService.getUserProvider(tenant);
    logger.debug("Looking up usersourcedId with user id {}",launchRequest.getUser_id());
    String userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, launchRequest.getUser_id());
    
    if (StringUtils.isBlank(userSourcedId)) {
      if (launchRequest.getCustom() != null && !launchRequest.getCustom().isEmpty()) {        
        String custom_canvas_user_id = launchRequest.getCustom().get("custom_canvas_user_id");
        if (StringUtils.isNotBlank(custom_canvas_user_id)) {
          logger.debug("Looking up usersourcedId with custom_canvas_user_id {}",custom_canvas_user_id);
          userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, custom_canvas_user_id);
        }
      }
      
      if (StringUtils.isBlank(userSourcedId)) {
        if (launchRequest.getExt() != null && !launchRequest.getExt().isEmpty()) {        
          String ext_user_username = launchRequest.getExt().get("ext_user_username");
          if (StringUtils.isNotBlank(ext_user_username)) {
            logger.debug("Looking up usersourcedId with ext_user_username {}",ext_user_username);
            userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, ext_user_username);
          }
        }
      }
      
      if (StringUtils.isBlank(userSourcedId)) {
        if (StringUtils.isNotBlank(launchRequest.getLis_person_sourcedid())) {        
          logger.debug("Looking up usersourcedId with lis_person_sourcedid {}",launchRequest.getLis_person_sourcedid());
          userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, launchRequest.getLis_person_sourcedid());
        }
      }

      if (StringUtils.isBlank(userSourcedId)) {
        if (launchRequest.getExt() != null && !launchRequest.getExt().isEmpty()) {        
          String ext_sakai_eid = launchRequest.getExt().get("ext_sakai_eid");
          if (StringUtils.isNotBlank(ext_sakai_eid)) {
            logger.debug("Looking up usersourcedId with ext_sakai_eid {}",ext_sakai_eid);
            userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, ext_sakai_eid);
          }
        }
      }

    }
    
    if (StringUtils.isBlank(userSourcedId)) {
      throw new ProviderException("No user mapping found");
    }
    
    // this is an ugly but effective workaround
    launchRequest.setUser_id(userSourcedId);
    
    // Find class mapping
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);
    logger.debug("Looking up class sourcedId with context id {}",launchRequest.getContext_id());
    String classSourcedId = courseProvider.getClassSourcedIdWithExternalId(tenant, launchRequest.getContext_id());
    
    if (StringUtils.isBlank(classSourcedId)) {
      if (launchRequest.getCustom() != null && StringUtils.isNotBlank(launchRequest.getCustom().get("custom_canvas_course_id"))) {
        logger.debug("Looking up class sourcedId with custom_canvas_course_id {}", launchRequest.getCustom().get("custom_canvas_course_id"));
        classSourcedId = courseProvider.getClassSourcedIdWithExternalId(tenant, launchRequest.getCustom().get("custom_canvas_course_id"));
      }
      else if (launchRequest.getLis_course_section_sourcedid() != null) {
        logger.debug("Looking up class sourcedId with lis_course_section_sourcedid {}", launchRequest.getLis_course_section_sourcedid());
        classSourcedId = courseProvider.getClassSourcedIdWithExternalId(tenant, launchRequest.getLis_course_section_sourcedid());
      }
    }

    if (StringUtils.isBlank(classSourcedId)) {
      throw new ProviderException("No class mapping found");
    }
    
    // also an ugly but effective workaround
    launchRequest.setContext_id(classSourcedId);

    
    //return "index";
    String cmUrl = null;
    if (StringUtils.isNotBlank(classSourcedId)) {
      cmUrl = String.format("/direct/courselist/%s",PulseUtility.escapeForPulse(classSourcedId));
    }
    else {
      cmUrl = "/direct/courselist";
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

	//we look for the existing token because they might have already logged in via
	//the administration portal ('/login')... in which case we don't want to wipe away that
	//authority.
	if (sessionCookie != null && !StringUtils.isEmpty(sessionCookie.getValue())) {
		String jwtToken = sessionCookie.getValue();
		claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
		List<String> authoritiesList = (List<String>) claims.get("Authorities");
		authoritiesSet = new HashSet<String>(authoritiesList);
	}
    
    //currently, users are only launching via LTI with a instructor or student role.
    if (LTIEntryPointController.hasInstructorRole(null, launchRequest.getRoles())) {
    	authoritiesSet.add("ROLE_INSTRUCTOR");
    } 
    else if (LTIEntryPointController.hasLearnerRole(null, launchRequest.getRoles())) {
    	authoritiesSet.add("ROLE_STUDENT");
    }     
    else {
      throw new UnauthorizedUserException("Does not have the instructor or learner role");
    }
    
        
    //Set the JWT Token here
    claims = new HashMap<>();
    claims.put("tenantId", tenant.getId());
    claims.put("userSourcedId", userSourcedId);
    claims.put("classSourceId", launchRequest.getContext_id());
    claims.put("launchRequest", launchRequest.toJSON());
    claims.put("userEmail", launchRequest.getLis_person_contact_email_primary());
    claims.put("Authorities", authoritiesSet);
    
    final String jwtToken = jwtTokenUtil.generateToken(claims);
    
    Cookie cookie = new Cookie("securityToken", jwtToken);    
    cookie.setDomain(baseDomain);
    response.addCookie(cookie);

    response.addHeader("Access-Control-Allow-Origin", "*");
    
String courseId = PulseUtility.escapeForPulse(launchRequest.getContext_id());
    return new RedirectView(uxBaseUrl + "/courselist/" + courseId);
  }

  public static boolean hasLearnerRole(List<String> studentRoles, String roles) {

	    if (studentRoles == null) {
	    	studentRoles = new ArrayList<>();
	    	studentRoles.add("Learner");
	    	studentRoles.add("Student");
	    }

	    for (String instructorRole : studentRoles) {
	      if (roles.contains(instructorRole)) {
	        return true;
	      }
	    }

	    return false;
	  }
   

public static boolean hasInstructorRole(List<String> instructorRoles, String roles) {

    if (instructorRoles == null) {
      instructorRoles = new ArrayList<>();
      instructorRoles.add("Instructor");
      instructorRoles.add("ContentDeveloper");
      instructorRoles.add("Administrator");
      instructorRoles.add("TeachingAssistant");
      instructorRoles.add("Teacher");
      instructorRoles.add("Faculty");
    }

    for (String instructorRole : instructorRoles) {
      if (roles.contains(instructorRole)) {
        return true;
      }
    }

    return false;
  }
}
