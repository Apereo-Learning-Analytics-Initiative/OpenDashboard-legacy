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
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;
import od.exception.MissingTenantException;
import od.framework.model.Consumer;
import od.framework.model.Tenant;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

//  @Value("${auth.oauth.key}")
//  private String key;
//  @Value("${auth.oauth.secret}")
//  private String secret;
  
  @Autowired private MongoTenantRepository tenantRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res
		  							, FilterChain fc) throws ServletException, IOException {
    	  
	logger.debug("In OAuthFilter");
    logger.debug(req.getMethod());
    
    // Only apply the filter to LTI tool launches (HTTP POSTS)
    // Seems like there would be a way to configure the filter to 
    // match URL and HTTP Method - Maybe this moves to a controller (TODO - research)
    if (HttpMethod.POST.toString().equalsIgnoreCase(req.getMethod())) {
      LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
      SortedMap<String, String> alphaSortedMap = launchRequest.toSortedMap();
      String signature = alphaSortedMap.remove(OAuthUtil.SIGNATURE_PARAM);      
      String consumerKey = alphaSortedMap.get(OAuthUtil.CONSUMER_KEY_PARAM);
      
      Tenant tenant = tenantRepository.findByConsumersOauthConsumerKey(consumerKey);
      
      
      
      if (tenant == null) {
        res.sendRedirect("/errorpage");
        return;
      }
      
      Set<Consumer> consumers = tenant.getConsumers();
      
      if (consumers == null || consumers.isEmpty()) {
        throw new MissingTenantException("OAUTH_MISSING_KEY");
      }
      
      List<Consumer> consumer = consumers.stream().filter(c -> c.getOauthConsumerKey().equals(consumerKey)).collect(Collectors.toList());

      String calculatedSignature;
      String url = req.getRequestURL().toString();
      String secret = consumer.get(0).getOauthConsumerSecret();
      String algorithm = OAuthUtil.mapToJava(alphaSortedMap.get(OAuthUtil.SIGNATURE_METHOD_PARAM));
      try {
        calculatedSignature =  new OAuthMessageSigner().sign(secret, algorithm , "POST", url, alphaSortedMap);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        throw new ServletException("OAUTH_CALCULATION_ERROR");
      }

      // retry by switch prefix between https and http
      if (!signature.equals(calculatedSignature)) {
        String recalculatedSignature = null;

        // switch http to https or https to http 
    	// in case there was a proxy conversion
		if (StringUtils.startsWithIgnoreCase(url, "http:")) {
		  url = StringUtils.replaceOnce(url, "http:", "https:");
		}
		else {
			if (StringUtils.startsWithIgnoreCase(url, "https:")) {
		        url = StringUtils.replaceOnce(url, "https:", "http:");
		      }        	
		}
		try {
		    recalculatedSignature = new OAuthMessageSigner().sign(secret , algorithm , "POST", url, alphaSortedMap);
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		    throw new ServletException("OAUTH_CALCULATION_ERROR");
		}
        if (!signature.equals(recalculatedSignature)) {
            throw new AuthorizationServiceException("OAUTH_SIGNATURE_MISMATCH");
          }
      }
    }
    
    fc.doFilter(req, res);
  }

}
