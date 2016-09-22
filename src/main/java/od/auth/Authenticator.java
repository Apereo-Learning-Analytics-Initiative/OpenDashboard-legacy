/**
 * 
 */
package od.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author ggilbert
 *
 */
public interface Authenticator {
  Authentication authenticate(Authentication token) throws AuthenticationException;
}
