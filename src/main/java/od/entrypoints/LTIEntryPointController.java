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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lti.LaunchRequest;
import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.Tenant;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.ContextMappingRepository;
import od.repository.mongo.MongoTenantRepository;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  
  @Autowired
  //@Qualifier(value="LTIAuthenticationManager")
  private AuthenticationManager authenticationManager;
  
  @RequestMapping(value = { "/lti" }, method = RequestMethod.POST)
  public String lti(HttpServletRequest request) throws ProviderException, ProviderDataConfigurationException {
    LaunchRequest launchRequest = new LaunchRequest(request.getParameterMap());
    
    logger.debug("{}",launchRequest);
    
    String consumerKey = launchRequest.getOauth_consumer_key();
    String contextId = launchRequest.getContext_id();
    
    Tenant tenant = mongoTenantRepository.findByConsumersOauthConsumerKey(consumerKey);

    // Create a token using spring provided class : LTIAuthenticationToken
    String role;
    if (LTIEntryPointController.hasInstructorRole(null, launchRequest.getRoles())) {
      role = "ROLE_INSTRUCTOR";
    } else {
      throw new UnauthorizedUserException("Does not have the instructor role");
      //role = "ROLE_STUDENT";
    }
    
    if (launchRequest.getCustom() != null && !launchRequest.getCustom().isEmpty()) {
      String canvasCourseId = launchRequest.getCustom().get("custom_canvas_course_id");
      
      if (StringUtils.isNotBlank(canvasCourseId)) {
        contextId = canvasCourseId;
        
        String userId = launchRequest.getCustom().get("custom_canvas_user_id");
        UserProvider userProvider = providerService.getUserProvider(tenant);
        String userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, userId);
        
        // this is an ugly but effective workaround
        launchRequest.setUser_id(userSourcedId);
      }
    }

    
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);
        
    logger.debug("Looking up class sourcedId with context id {}",contextId);
    
    String classId = courseProvider.getClassSourcedIdWithExternalId(tenant, contextId);
    // also an ugly but effective workaround
    launchRequest.setContext_id(classId);

    OpenDashboardAuthenticationToken token = new OpenDashboardAuthenticationToken(launchRequest, 
        null,
        tenant.getId(), 
        null, 
        null,
        AuthorityUtils.commaSeparatedStringToAuthorityList(role));

    // generate session if one doesn't exist
    request.getSession();

    // save details as WebAuthenticationDetails records the remote address and
    // will also set the session Id if a session already exists (it won't create
    // one).
    token.setDetails(new WebAuthenticationDetails(request));

    // authenticationManager injected as spring bean, : LTIAuthenticationProvider
    Authentication authentication = authenticationManager.authenticate(token);

    // Need to set this as thread locale as available throughout
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Set SPRING_SECURITY_CONTEXT attribute in session as Spring identifies
    // context through this attribute
    request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

    //return "index";
    String cmUrl = null;
    if (StringUtils.isNotBlank(classId)) {
      cmUrl = String.format("/direct/courselist/%s",classId);
    }
    else {
      cmUrl = "/direct/courselist";
    }
    
    return "redirect:"+cmUrl;
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
