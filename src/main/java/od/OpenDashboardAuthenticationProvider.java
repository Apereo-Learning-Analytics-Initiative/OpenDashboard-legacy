/**
 * 
 */
package od;

import od.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class OpenDashboardAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired private AuthService authService;
  
  
  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }


  @Override
  protected void additionalAuthenticationChecks(UserDetails arg0, UsernamePasswordAuthenticationToken arg1) throws AuthenticationException {
    // No-op
  }


  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token) throws AuthenticationException {
    return authService.getDefaultAuthProvider().getUserDetails(username, token);
  }

}
