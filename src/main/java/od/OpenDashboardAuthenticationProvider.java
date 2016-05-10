/**
 * 
 */
package od;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.Map;

import od.providers.ProviderException;
import od.providers.course.CourseProvider;
import od.providers.course.learninglocker.LearningLockerStaff;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component
public class OpenDashboardAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  final static Logger log = LoggerFactory.getLogger(OpenDashboardAuthenticationProvider.class);
  
  @Value("${ll.use.demo:false}")
  protected boolean DEMO = false;
  
  @Autowired private CourseProvider courseProvider;
  @Autowired private MongoTenantRepository mongoTenantRepository;

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
      String pid = data.get("pid");
      log.info("pid: {}", pid);
      
      if (DEMO) {
        user = new OpenDashboardUser(pid, pid, 
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"), StringUtils.substringAfter(username, "TENANTID:"), null);
      }
      else {
        try {
          LearningLockerStaff staff = courseProvider.getStaffWithPid(mongoTenantRepository.findOne(StringUtils.substringAfter(username, "TENANTID:")), pid);
          user = new OpenDashboardUser(staff.getStaffId(), pid, 
              AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_INSTRUCTOR"), StringUtils.substringAfter(username, "TENANTID:"), null);
        } 
        catch (ProviderException e) {
          log.error(e.getMessage(),e);
          throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
        }
      }
      
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
