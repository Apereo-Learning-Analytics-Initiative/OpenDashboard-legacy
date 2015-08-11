/**
 *
 */
package od.repository.mongo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import lti.LaunchRequest;
import od.exception.MissingCookieException;
import od.utils.AppControllerAdvice;

/**
 * @author jbrown
 *
 */
@Profile("mongo-multitenant")
@Component
public class MongoMultiTenantFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(MongoMultiTenantFilter.class);
  
  @Value("${od.tenantCookieName:OD_T}")
  private String cookieName;
  
  @Autowired
  CookieGenerator cookieGenerator;
  
  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
    logger.debug("applying MongoMultiTenantFilter");
    
    String databaseName = null;
    MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    
    LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
    
    if (launchRequest != null && StringUtils.isNotBlank(launchRequest.getOauth_consumer_key())) {
      logger.debug("is LTI launch");
      databaseName = launchRequest.getOauth_consumer_key();
      cookieGenerator.setCookieName(cookieName);
      cookieGenerator.setCookiePath("/");
      cookieGenerator.setCookieMaxAge(86400);
      cookieGenerator.addCookie(res, Base64.encodeBase64String(databaseName.getBytes("UTF-8")));
    }
    else {
      logger.debug("not LTI launch");
      Cookie cookie = WebUtils.getCookie(req, cookieName);
      if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
        logger.debug("got cookie: "+cookieName);
        databaseName = new String(Base64.decodeBase64(cookie.getValue()));
      }
      else {
        throw new MissingCookieException(cookieName);
      }
    }
    
    if (StringUtils.isNotBlank(databaseName)) {
      logger.info("setting database name: " + databaseName);
      MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(databaseName);
    }
    else {
      logger.info("using default database");
    }

    fc.doFilter(req, res);
  }
}
