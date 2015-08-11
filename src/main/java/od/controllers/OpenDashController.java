/**
 *
 */
package od.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import lti.LaunchRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ggilbert
 *
 */
@Controller
public class OpenDashController {
  private static final Logger logger = LoggerFactory.getLogger(OpenDashController.class);

  @Autowired
  private AuthenticationManager authenticationManager;

  @RequestMapping(value = { "/" }, method = RequestMethod.POST)
  public String lti(HttpServletRequest request, Model model) {
    LaunchRequest launchRequest = new LaunchRequest(request.getParameterMap());
    model.addAttribute("inbound_lti_launch_request", launchRequest);

    String uuid = UUID.randomUUID().toString();
    model.addAttribute("token", uuid);

    // Create a token using spring provided class :
    // org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    String role = null;
    if (hasInstructorRole(null, launchRequest.getRoles())) {
      role = "ROLE_INSTRUCTOR";
    } else {
      role = "ROLE_STUDENT";
    }

    String credentials = launchRequest.getUser_id() + ":" + uuid + ":" + role;

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials, uuid,
        AuthorityUtils.commaSeparatedStringToAuthorityList(role));

    // generate session if one doesn't exist
    request.getSession();

    // save details as WebAuthenticationDetails records the remote address and
    // will also set the session Id if a session already exists (it won't create
    // one).
    token.setDetails(new WebAuthenticationDetails(request));

    // authenticationManager injected as spring bean, you can use custom or
    // spring provided authentication manager
    Authentication authentication = authenticationManager.authenticate(token);

    // Need to set this as thread locale as available throughout
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Set SPRING_SECURITY_CONTEXT attribute in session as Spring identifies
    // context through this attribute
    request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

    return "od";
  }

  @Secured({ "ROLE_INSTRUCTOR", "ROLE_STUDENT" })
  @RequestMapping(value = { "/", "/cm/**" }, method = RequestMethod.GET)
  public String routes(Model model) {
    return "od";
  }

  public boolean hasInstructorRole(List<String> instructorRoles, String roles) {

    if (instructorRoles == null) {
      instructorRoles = new ArrayList<String>();
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

  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }

  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }
}
