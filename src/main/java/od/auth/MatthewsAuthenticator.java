/**
 * 
 */
package od.auth;

import od.framework.model.Tenant;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@ConditionalOnProperty(name={"opendashboard.authenticator"}, havingValue="MatthewsAuthenticator")
@Component("MatthewsAuthenticator")
public class MatthewsAuthenticator implements Authenticator {

  final static Logger log = LoggerFactory.getLogger(DemoAuthenticator.class);
  
  @Value("${od.admin.user:admin}")
  private String adminUsername;
  
  @Value("${od.admin.password:admin}")
  private String adminPassword;
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private ProviderService providerService;

  @Override
  public Authentication authenticate(Authentication token) throws AuthenticationException {
    log.debug("{}", token);
    OpenDashboardAuthenticationToken authToken = null;
    
    if (token instanceof OpenDashboardAuthenticationToken) {
      OpenDashboardAuthenticationToken odToken = (OpenDashboardAuthenticationToken)token;
      
      Tenant tenant = mongoTenantRepository.findOne(odToken.getTenantId());
      UserProvider userProvider;
      
      try {
        userProvider = providerService.getUserProvider(tenant);
      } 
      catch (ProviderDataConfigurationException e) {
        throw new AuthenticationServiceException("No user provider configured");
      }
      
      if (odToken.getLaunchRequest() != null) {
        log.debug("is lti");
        
        String userSourcedId;
        try {
          userSourcedId = userProvider.getUserSourcedIdWithExternalId(tenant, odToken.getLaunchRequest().getUser_id());
        } 
        catch (ProviderException e) {
          throw new AuthenticationServiceException("Unable to access user provider");
        }
        
        if (StringUtils.isBlank(userSourcedId)) {
          throw new AuthenticationServiceException("UserSourcedId Not Found");
        }
        
        authToken = new OpenDashboardAuthenticationToken(odToken.getLaunchRequest(), 
            null,
            odToken.getTenantId(), 
            new OpenDashboardUser(odToken.getLaunchRequest().getUser_id(), 
                userSourcedId, 
                odToken.getAuthorities(), 
                odToken.getTenantId(), 
                odToken.getLaunchRequest()), 
                userSourcedId,
                odToken.getAuthorities(),
                odToken.getUserEmail());
      }
      else {
        throw new AuthenticationServiceException("Non-LTI access not permitted");
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
                adminUsername, 
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"), 
                null, 
                null), 
                adminUsername,
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"),
            "NA");
      }
      
    }
    
    return authToken;
  }

}
