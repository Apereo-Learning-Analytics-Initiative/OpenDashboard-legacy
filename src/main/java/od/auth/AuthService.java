/**
 * 
 */
package od.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class AuthService {
  
  @Autowired private Map<String, AuthProvider> authProviders;
  
  public AuthProvider getDefaultAuthProvider() {
    return authProviders.get("DEFAULT");
  }

}
