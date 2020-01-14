/**
 * 
 */
package od.entrypoints;

import javax.servlet.http.HttpServletRequest;

import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.Tenant;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@ConditionalOnProperty(name={"opendashboard.entrypoint"}, havingValue="DemoEntryPointProcessor")
@Component("DemoEntryPointProcessor")
public class DemoEntryPointProcessor implements EntryPointProcessor {
  
  private static final Logger logger = LoggerFactory.getLogger(DemoEntryPointProcessor.class);
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private AuthenticationManager authenticationManager;

  @Override
  public String post(HttpServletRequest request, String tenantId) {
    return "redirect:/jwtlogin/".concat(tenantId);
  }

  @Override
  public String get(HttpServletRequest request, String tenantId) {
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    
    // Create a token using spring provided class :
    // org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    OpenDashboardAuthenticationToken authToken = new OpenDashboardAuthenticationToken(null, null, tenantId, null, null, null, null);

    // generate session if one doesn't exist
    request.getSession();

    // save details as WebAuthenticationDetails records the remote address and
    // will also set the session Id if a session already exists (it won't
    // create one).
    authToken.setDetails(new WebAuthenticationDetails(request));

    // authenticationManager injected as spring bean, you can use custom or
    // spring provided authentication manager
    Authentication authentication = null;
    try {
      authentication = authenticationManager.authenticate(authToken);
    } 
    catch (AuthenticationException e) {
      logger.error(e.getMessage(),e);
      return "redirect:/error";
    }

    // Need to set this as thread locale as available throughout
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Set SPRING_SECURITY_CONTEXT attribute in session as Spring identifies
    // context through this attribute
    request.getSession().setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    return "redirect:/direct/courselist";
  }

}
