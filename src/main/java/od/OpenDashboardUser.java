/**
 * 
 */
package od;

import java.util.Collection;

import lti.LaunchRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * @author ggilbert
 *
 */
public class OpenDashboardUser extends User {

  private static final long serialVersionUID = 1L;
  
  private String tenantId;
  private LaunchRequest launchRequest;

  public OpenDashboardUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
      boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
  }
  
  public OpenDashboardUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String tenantId, LaunchRequest launchRequest) {
    this(username, password, true, true, true, true, authorities);
    
    this.tenantId = tenantId;
    this.launchRequest = launchRequest;
  }

  public String getTenantId() {
    return tenantId;
  }

  public LaunchRequest getLaunchRequest() {
    return launchRequest;
  }


}
