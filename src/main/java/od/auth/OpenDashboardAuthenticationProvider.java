/**
 * 
 */
package od.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class OpenDashboardAuthenticationProvider implements AuthenticationProvider {

  final static Logger log = LoggerFactory.getLogger(OpenDashboardAuthenticationProvider.class);
  
  @Autowired private Authenticator authenticator;
  
  @Override
  public boolean supports(Class<?> authentication) {
    log.debug("{}",OpenDashboardAuthenticationToken.class.isAssignableFrom(authentication));
    return (OpenDashboardAuthenticationToken.class.isAssignableFrom(authentication) || UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

  @Override
  public Authentication authenticate(Authentication token) throws AuthenticationException {
    return authenticator.authenticate(token);
  }

}
