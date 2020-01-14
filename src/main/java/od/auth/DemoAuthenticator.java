package od.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name={"opendashboard.authenticator"}, havingValue="DemoAuthenticator")
@Component("DemoAuthenticator")
public class DemoAuthenticator implements Authenticator {
  
  final static Logger log = LoggerFactory.getLogger(DemoAuthenticator.class);
  
  @Value("${od.admin.user:admin}")
  private String adminUsername;
  
  @Value("${od.admin.password:admin}")
  private String adminPassword;

  @Override
  public Authentication authenticate(Authentication token) throws AuthenticationException {
    log.debug("{}", token);
    OpenDashboardAuthenticationToken authToken = null;
    String uuid = "teacher-sourcedId-1";
    
    if (token instanceof OpenDashboardAuthenticationToken) {
      OpenDashboardAuthenticationToken odToken = (OpenDashboardAuthenticationToken)token;
      
      if (odToken.getLaunchRequest() != null) {
        log.debug("is lti");
        
        authToken = new OpenDashboardAuthenticationToken(odToken.getLaunchRequest(), 
            null,
            odToken.getTenantId(), 
            new OpenDashboardUser(odToken.getLaunchRequest().getUser_id(), 
                uuid, 
                odToken.getAuthorities(), 
                odToken.getTenantId(), 
                odToken.getLaunchRequest()), 
            uuid,
            odToken.getAuthorities(),
            odToken.getUserEmail());
      }
      else {
        authToken = new OpenDashboardAuthenticationToken(null, 
            null,
            odToken.getTenantId(), 
            new OpenDashboardUser(uuid, 
                uuid, 
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"), 
                odToken.getTenantId(), 
                null), 
            uuid,
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"),
            odToken.getUserEmail());

      }
    }
    else if (token instanceof UsernamePasswordAuthenticationToken) {
      log.debug("not opendashboardauthenticationtoken");
      UsernamePasswordAuthenticationToken upToken = (UsernamePasswordAuthenticationToken)token;
      
      if (upToken.getPrincipal().equals(adminUsername) && 
          upToken.getCredentials().equals(adminPassword)) {
        authToken = new OpenDashboardAuthenticationToken(null, 
            null,
            null, 
            new OpenDashboardUser(adminUsername, 
                uuid, 
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"), 
                null, 
                null), 
            uuid,
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"),
            "NA");
      }
      
    }
    
    return authToken;
  }

}
