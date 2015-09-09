/**
 * 
 */
package od.lti;

import java.util.Collection;

import lti.LaunchRequest;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author ggilbert
 *
 */
public class LTIAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private Logger log = Logger.getLogger(LTIAuthenticationToken.class);
  private static final long serialVersionUID = 1L;
  
  private LaunchRequest launchRequest;
  private String tenantId;

  public LTIAuthenticationToken(LaunchRequest launchRequest, String tenantId, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.launchRequest = launchRequest;
    this.tenantId = tenantId;
  }

  public LaunchRequest getLaunchRequest() {
    return launchRequest;
  }

  public String getTenantId() {
    return tenantId;
  }

}
