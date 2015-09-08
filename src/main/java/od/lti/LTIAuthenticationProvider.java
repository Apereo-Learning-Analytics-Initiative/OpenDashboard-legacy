/**
 * 
 */
package od.lti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class LTIAuthenticationProvider implements AuthenticationProvider {
  final static Logger log = LoggerFactory.getLogger(LTIAuthenticationProvider.class);

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    log.debug(authentication.toString());
    // Nothing to do, just return it
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(LTIAuthenticationToken.class);
  }

}
