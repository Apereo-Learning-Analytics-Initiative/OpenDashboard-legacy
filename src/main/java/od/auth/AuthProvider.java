/**
 * 
 */
package od.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author ggilbert
 *
 */
public interface AuthProvider {
  UserDetails getUserDetails(String username, UsernamePasswordAuthenticationToken token) throws AuthenticationException;
}
