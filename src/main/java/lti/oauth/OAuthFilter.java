/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
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
import org.springframework.http.HttpMethod;
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
    logger.debug(req.getMethod());
    
    // Only apply the filter to LTI tool launches (HTTP POSTS)
    // Seems like there would be a way to configure the filter to 
    // match URL and HTTP Method - Maybe this moves to a controller (TODO - research)
    if (HttpMethod.POST.toString().equalsIgnoreCase(req.getMethod())) {
      LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
      SortedMap<String, String> alphaSortedMap = launchRequest.toSortedMap();
      String signature = alphaSortedMap.remove(OAuthUtil.SIGNATURE_PARAM);

      String calculatedSignature;
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
    }
    
    fc.doFilter(req, res);
  }

}
