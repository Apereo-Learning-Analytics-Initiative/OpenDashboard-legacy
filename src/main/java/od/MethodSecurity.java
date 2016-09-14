/**
 * 
 */
package od;

import od.auth.OpenDashboardAuthenticationToken;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class MethodSecurity {
  final static Logger log = LoggerFactory.getLogger(MethodSecurity.class);
  
  public boolean checkTenant(Authentication authentication, String tenantId) {
    
    if (authentication == null || StringUtils.isBlank(tenantId)) {
      throw new IllegalArgumentException("Arguments cannot be null or empty");
    }
    
    log.debug("{}",authentication);
    log.debug(tenantId);
    
    OpenDashboardAuthenticationToken odToken = (OpenDashboardAuthenticationToken)authentication;
    
    if (StringUtils.isBlank(odToken.getTenantId()) || !odToken.getTenantId().equals(tenantId)) {
      return false;
    }
    
    return true;
  }
}
