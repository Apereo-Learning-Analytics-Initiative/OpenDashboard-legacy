package od.auth.jisc;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.Map;
import java.util.UUID;

import od.auth.Authenticator;
import od.auth.OpenDashboardAuthenticationToken;
import od.auth.OpenDashboardUser;
import od.framework.model.Tenant;
import od.providers.ProviderService;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnProperty(name={"opendashboard.authenticator"}, havingValue="JiscAuthenticator")
@Component("JiscAuthenticator")
public class JiscAuthenticator implements Authenticator {
  
  final static Logger log = LoggerFactory.getLogger(JiscAuthenticator.class);
  
  @Value("${ukfederation.role:staff}")
  private String validRole;
  
  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;

  @Value("${od.admin.user:admin}")
  private String adminUsername;
  
  @Value("${od.admin.password:admin}")
  private String adminPassword;
  
  @Value("${jwt.key:xasdsadmdscasd!!!}")
  private String jwtKey;

  @Override
  public Authentication authenticate(Authentication token) throws AuthenticationException {
    log.debug("{}", token);
    OpenDashboardAuthenticationToken authToken = null;
    String uuid = UUID.randomUUID().toString();
    
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
        log.debug("is jwt");
        
        String jwtJson = odToken.getJwtToken();
        // parse out the token
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
          actualObj = mapper.readTree(jwtJson);
        } 
        catch (Exception e) {
          log.error(e.getMessage(),e);
          throw new AuthenticationServiceException(e.getMessage());
        } 
        log.info("{}", actualObj.get("jwt").textValue());
        
        // parse the token into claims
        Jws<Claims> claims = null;
        try {
          claims = Jwts.parser().setSigningKey(jwtKey.getBytes("UTF-8")).parseClaimsJws(actualObj.get("jwt").textValue());
        } 
        catch (Exception e) {
          log.error(e.getMessage(),e);
          throw new AuthenticationServiceException(e.getMessage());
        } 
        
        log.info("data: {}", claims.getBody().get("data"));
        Map<String, String> data = (Map<String, String>)claims.getBody().get("data");
        log.info("data map: {}", data);
        String eppn = data.get("eppn");
        log.info("eppn: {}", eppn);
        String affiliation = data.get("affiliation");
        log.info("affiliation: {}",affiliation);
        
        if (StringUtils.isBlank(affiliation) || !StringUtils.contains(affiliation, validRole)) {
          throw new InsufficientAuthenticationException(String.format("Invalid affiliation: {}",affiliation));
        }
        
        try {
          Tenant tenant = mongoTenantRepository.findOne(odToken.getTenantId());
          UserProvider userProvider = providerService.getUserProvider(tenant);
          String staffId = userProvider.getUserSourcedIdWithExternalId(tenant, eppn);
          authToken = new OpenDashboardAuthenticationToken(null, 
              null,
              odToken.getTenantId(), 
              new OpenDashboardUser(staffId, 
                  uuid, 
                  AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"), 
                  odToken.getTenantId(), 
                  null), 
              uuid,
              AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"),
              odToken.getUserEmail());

        } 
        catch (Exception e) {
          log.error(e.getMessage(),e);
          throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
        } 
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
