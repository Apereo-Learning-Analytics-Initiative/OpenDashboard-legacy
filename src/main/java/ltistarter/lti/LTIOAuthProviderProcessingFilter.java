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
import java.io.IOException;

/**
 * LTI compatible Zero Legged OAuth processing servlet filter
 */
public class LTIOAuthProviderProcessingFilter extends ProtectedResourceProcessingFilter {

    public LTIOAuthProviderProcessingFilter(LTIConsumerDetailsService oAuthConsumerDetailsService, MyOAuthNonceServices oAuthNonceServices, OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint, LTIOAuthAuthenticationHandler oAuthAuthenticationHandler, OAuthProviderTokenServices oAuthProviderTokenServices) {
        super();
        setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
        setAuthHandler(oAuthAuthenticationHandler);
        setConsumerDetailsService(oAuthConsumerDetailsService);
        setNonceServices(oAuthNonceServices);
        setTokenServices(oAuthProviderTokenServices);
        //setIgnoreMissingCredentials(false); // die if OAuth params are not included
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (!LTIRequest.isLTIRequest(servletRequest)) {
            // NOTE: tsugi handles this by just allowing the request to continue - since we have a dedicated endpoint for launches I am killing it -AZ
            throw new IllegalStateException("Request is not a well formed LTI request (invalid lti_version or lti_message_type)");
        }
        super.doFilter(servletRequest, servletResponse, chain);
    }
}
