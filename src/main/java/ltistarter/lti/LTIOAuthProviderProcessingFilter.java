/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.lti;

import ltistarter.oauth.MyOAuthNonceServices;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * LTI compatible Zero Legged OAuth processing servlet filter
 */
public class LTIOAuthProviderProcessingFilter extends ProtectedResourceProcessingFilter {

    LTIDataService ltiDataService;

    public LTIOAuthProviderProcessingFilter(LTIDataService ltiDataService, LTIConsumerDetailsService oAuthConsumerDetailsService, MyOAuthNonceServices oAuthNonceServices, OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint, LTIOAuthAuthenticationHandler oAuthAuthenticationHandler, OAuthProviderTokenServices oAuthProviderTokenServices) {
        super();
        assert ltiDataService != null;
        this.ltiDataService = ltiDataService;
        setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
        setAuthHandler(oAuthAuthenticationHandler);
        setConsumerDetailsService(oAuthConsumerDetailsService);
        setNonceServices(oAuthNonceServices);
        setTokenServices(oAuthProviderTokenServices);
        //setIgnoreMissingCredentials(false); // die if OAuth params are not included
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // NOTE: tsugi handles failures by just allowing the request to continue - since we have a dedicated endpoint for launches the LTIRequest object will throw an IllegalStateException is the LTI request is invalid somehow
        if (!(servletRequest instanceof HttpServletRequest)) {
            throw new IllegalStateException("LTI request MUST be an HttpServletRequest (cannot only be a ServletRequest)");
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // load and initialize the LTI request object (loads and validates the data)
        LTIRequest ltiRequest = new LTIRequest(httpServletRequest, ltiDataService, true); // IllegalStateException if invalid
        httpServletRequest.setAttribute("LTI", true); // indicate this request is an LTI one
        httpServletRequest.setAttribute("lti_valid", ltiRequest.isLoaded() && ltiRequest.isComplete()); // is LTI request totally valid and complete
        httpServletRequest.setAttribute(LTIRequest.class.getName(), ltiRequest); // make the LTI data accessible later in the request if needed
        super.doFilter(servletRequest, servletResponse, chain);
    }

}
