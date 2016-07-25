/**
 * 
 */
package od.auth;

import od.OpenDashboardUser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("DEFAULT")
public class DefaultAuthProvider implements AuthProvider {
  
  @Value("${od.admin.user:admin}")
  private String adminUsername;
  
  @Value("${od.admin.password:admin}")
  private String adminPassword;

  @Override
  public UserDetails getUserDetails(String username, UsernamePasswordAuthenticationToken token) throws AuthenticationException {
    
    OpenDashboardUser user = null;
    
    if (StringUtils.isNotBlank(username)
        && username.equals(adminUsername)
        && token.getCredentials() != null
        && ((String)token.getCredentials()).equals(adminPassword)) {
      user = new OpenDashboardUser(adminUsername, adminPassword, 
          AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"), null, null);
    }
    
    return user;
  }

}
