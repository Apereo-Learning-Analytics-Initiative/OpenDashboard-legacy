/**
 *
 */
package lti.oauth;

import java.io.IOException;
import java.util.SortedMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author ggilbert
 *
 */
@Component
public class OAuthFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(OAuthFilter.class);

  @Value("${auth.oauth.key}")
  private String key;
  @Value("${auth.oauth.secret}")
  private String secret;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
    logger.debug("In OAuthFilter");
    LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
    SortedMap<String, String> alphaSortedMap = launchRequest.toSortedMap();
    String signature = alphaSortedMap.remove(OAuthUtil.SIGNATURE_PARAM);

    String calculatedSignature = null;
    try {
      calculatedSignature = new OAuthMessageSigner().sign(secret, OAuthUtil.mapToJava(alphaSortedMap.get(OAuthUtil.SIGNATURE_METHOD_PARAM)), "POST",
          req.getRequestURL().toString(), alphaSortedMap);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServletException("OAUTH_CALCULATION_ERROR");
    }

    if (!signature.equals(calculatedSignature)) {
      // try again with http
      String recalculatedSignature = null;

      if (StringUtils.startsWithIgnoreCase(req.getRequestURL().toString(), "https:")) {
        String url = StringUtils.replaceOnce(req.getRequestURL().toString(), "https:", "http:");
        try {
          recalculatedSignature = new OAuthMessageSigner().sign(secret, OAuthUtil.mapToJava(alphaSortedMap.get(OAuthUtil.SIGNATURE_METHOD_PARAM)),
              "POST", url, alphaSortedMap);
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          throw new ServletException("OAUTH_CALCULATION_ERROR");
        }
      }

      if (!signature.equals(recalculatedSignature)) {
        throw new AuthorizationServiceException("OAUTH_SIGNATURE_MISMATCH");
      }
    }

    fc.doFilter(req, res);
  }

}
