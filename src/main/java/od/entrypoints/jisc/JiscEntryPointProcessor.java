package od.entrypoints.jisc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import od.auth.OpenDashboardAuthenticationToken;
import od.entrypoints.EntryPointProcessor;
import od.framework.model.Tenant;
import od.providers.ProviderException;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name={"opendashboard.entrypoint"}, havingValue="JiscEntryPointProcessor")
@Component("JiscEntryPointProcessor")
public class JiscEntryPointProcessor implements EntryPointProcessor {
  
  private static final Logger logger = LoggerFactory.getLogger(JiscEntryPointProcessor.class);
  
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private AuthenticationManager authenticationManager;
  
  @Value("${jwt.endpoint:https://sp.data.alpha.jisc.ac.uk/Shibboleth.sso/Login?}")
  private String jwtEndpoint;
  
  @Value("${jwt.target:https://sp.data.alpha.jisc.ac.uk/secure/auth-web.php}")
  private String jwtTarget;
  
  @Value("${jwt.regTarget:https://sp.data.alpha.jisc.ac.uk/secure/register/reg-staff.php}")
  private String jwtRegTarget;

  @Value("${jwt.returnUrl:http://localhost:8081/jwtlogin/<tid>}")
  private String jwtReturnUrl;
  
  @Value("${jwt.appId:123}")
  private String jwtAppId;

  @Override
  public String post(HttpServletRequest request, String tenantId) {
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    
    StringBuilder stringBuilder = new StringBuilder();
    try {
      stringBuilder.append(jwtEndpoint);
      stringBuilder.append("target=");
      stringBuilder.append(URLEncoder.encode(jwtTarget, "UTF-8"));
      stringBuilder.append("?returl=");
      stringBuilder.append(URLEncoder.encode(StringUtils.replace(jwtReturnUrl, "<tid>", tenant.getId()), "UTF-8"));
      stringBuilder.append("&entityID=");
      stringBuilder.append(URLEncoder.encode(tenant.getIdpEndpoint().toString(), "UTF-8"));
      stringBuilder.append("&app_id=");
      stringBuilder.append(URLEncoder.encode(jwtAppId, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage(),e);
      return "redirect:/error";
    }
    
    return "redirect:"+stringBuilder.toString();
  }

  @Override
  public String get(HttpServletRequest request, String tenantId) {
    String token = request.getParameter("token");
    Tenant tenant = mongoTenantRepository.findOne(tenantId);
    
    // Create a token using spring provided class :
    // org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    OpenDashboardAuthenticationToken authToken = new OpenDashboardAuthenticationToken(null, token, tenantId, null, null, null, null);

    // generate session if one doesn't exist
    request.getSession();

    // save details as WebAuthenticationDetails records the remote address and
    // will also set the session Id if a session already exists (it won't
    // create one).
    authToken.setDetails(new WebAuthenticationDetails(request));

    // authenticationManager injected as spring bean, you can use custom or
    // spring provided authentication manager
    Authentication authentication = null;
    try {
      authentication = authenticationManager.authenticate(authToken);
    } 
    catch (AuthenticationException e) {
      logger.error(e.getMessage(),e);
      
      if (e.getMessage().equals(ProviderException.NO_STAFF_ENTRY_ERROR_CODE)) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
          stringBuilder.append(jwtEndpoint);
          stringBuilder.append("target=");
          stringBuilder.append(URLEncoder.encode(jwtRegTarget, "UTF-8"));
          stringBuilder.append("&entityID=");
          stringBuilder.append(URLEncoder.encode(tenant.getIdpEndpoint().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
          logger.error(e1.getMessage(),e1);
          return "redirect:/error";
        }

        return "redirect:"+stringBuilder.toString();
      }
      else {
        return "redirect:/error";
      }
    }

    // Need to set this as thread locale as available throughout
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Set SPRING_SECURITY_CONTEXT attribute in session as Spring identifies
    // context through this attribute
    request.getSession().setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    
    return "redirect:/direct/courselist";
  }

}
