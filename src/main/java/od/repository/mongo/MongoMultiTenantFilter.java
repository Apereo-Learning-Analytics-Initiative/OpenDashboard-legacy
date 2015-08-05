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

import lti.LaunchRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 * @author jbrown
 *
 */
@Profile("mongo")
@Component
public class MongoMultiTenantFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(MongoMultiTenantFilter.class);
  
  @Value("${od.tenantCookieName:OD_T}")
  private String cookieName;
  
  //TODO - apply this or something similar so we can disable
  // the filter when we want to
  @Value("${od.tenantEnabled:false}")
  private boolean multiTenantEnabled;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
    logger.debug("applying MongoMultiTenantFilter");
    
    String databaseName = null;
    MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    
    LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
    
    if (launchRequest != null && StringUtils.isNotBlank(launchRequest.getOauth_consumer_key())) {
      logger.debug("is LTI launch");
      databaseName = launchRequest.getOauth_consumer_key();
      CookieGenerator cookieGenerator = new CookieGenerator();
      cookieGenerator.setCookieName(cookieName);
      cookieGenerator.setCookiePath("/");
      cookieGenerator.setCookieMaxAge(86400);
      cookieGenerator.addCookie(res, databaseName);
    }
    else {
      logger.debug("not LTI launch");
      Cookie cookie = WebUtils.getCookie(req, cookieName);
      if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
        logger.debug("got cookie: "+cookieName);
        databaseName = cookie.getValue();
      }
      else {
        // TODO for now we'll just use the default database name
        // but at eventually we may want to throw an exception here
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
