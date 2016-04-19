/**
 * 
 */
package od;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component
public class OpenDashboardAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  final static Logger log = LoggerFactory.getLogger(OpenDashboardAuthenticationProvider.class);

  @Value("${od.admin.user:admin}")
  private String adminUsername;
  
  @Value("${od.admin.password:admin}")
  private String adminPassword;
  
  @Value("${jwt.key:xasdsadmdscasd!!!}")
  private String jwtKey;

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }


  @Override
  protected void additionalAuthenticationChecks(UserDetails arg0, UsernamePasswordAuthenticationToken arg1) throws AuthenticationException {
    // No-op
  }


  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token) throws AuthenticationException {
    
    OpenDashboardUser user = null;
    if (StringUtils.isNotBlank(username) && username.startsWith("TENANTID:")) {
      String jwtJson = (String)token.getCredentials();
      // parse out the token
      ObjectMapper mapper = new ObjectMapper();
      JsonNode actualObj = null;
      try {
        actualObj = mapper.readTree(jwtJson);
      } 
      catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      log.info("{}", actualObj.get("jwt").textValue());
      
      // parse the token into claims
      Jws<Claims> claims = null;
      try {
        claims = Jwts.parser().setSigningKey(jwtKey.getBytes("UTF-8")).parseClaimsJws(actualObj.get("jwt").textValue());
      } 
      catch (ExpiredJwtException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (UnsupportedJwtException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (MalformedJwtException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (SignatureException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } 
      catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      
      log.info("data: {}", claims.getBody().get("data"));
      Map<String, String> data = (Map<String, String>)claims.getBody().get("data");
      log.info("data map: {}", data);
      String eppn = data.get("eppn");
      log.info("eppn: {}", eppn);
      
      // TODO look up Staff record

      user = new OpenDashboardUser(eppn, eppn, 
          AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"), StringUtils.substringAfter(username, "TENANTID:"), null);

    }
    else if (StringUtils.isNotBlank(username)
        && username.equals(adminUsername)
        && token.getCredentials() != null
        && ((String)token.getCredentials()).equals(adminPassword)) {
      user = new OpenDashboardUser(adminUsername, adminPassword, 
          AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR,ROLE_ADMIN"), null, null);
    }
    
    return user;
  }

}
