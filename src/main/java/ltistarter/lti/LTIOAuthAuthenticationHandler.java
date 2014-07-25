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

import ltistarter.oauth.MyOAuthAuthenticationHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

@Component
public class LTIOAuthAuthenticationHandler implements OAuthAuthenticationHandler {

    final static Logger log = LoggerFactory.getLogger(LTIOAuthAuthenticationHandler.class);

    public static SimpleGrantedAuthority userGA = new SimpleGrantedAuthority("ROLE_USER");
    public static SimpleGrantedAuthority learnerGA = new SimpleGrantedAuthority("ROLE_LEARNER");
    public static SimpleGrantedAuthority instructorGA = new SimpleGrantedAuthority("ROLE_INSTRUCTOR");
    public static SimpleGrantedAuthority adminGA = new SimpleGrantedAuthority("ROLE_ADMIN");

    @PostConstruct
    public void init() {
        log.info("INIT");
    }

    @Override
    public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication, OAuthAccessProviderToken authToken) {
        Collection<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        LTIRequest ltiRequest = (LTIRequest) request.getAttribute(LTIRequest.class.getName());
        if (ltiRequest == null) {
            throw new IllegalStateException("Cannot create authentication for LTI because the LTIRequest is null");
        }

        // attempt to create a user Authority
        String username = ltiRequest.getLtiUserId();
        if (StringUtils.isBlank(username)) {
            username = authentication.getName();
        }

        // set appropriate permissions for this user based on LTI data
        if (ltiRequest.getUser() != null) {
            authorities.add(userGA);
        }
        if (ltiRequest.isRoleAdministrator()) {
            authorities.add(adminGA);
        }
        if (ltiRequest.isRoleInstructor()) {
            authorities.add(instructorGA);
        }
        if (ltiRequest.isRoleLearner()) {
            authorities.add(learnerGA);
        }

        Principal principal = new MyOAuthAuthenticationHandler.NamedOAuthPrincipal(username, authorities,
                authentication.getConsumerCredentials().getConsumerKey(),
                authentication.getConsumerCredentials().getSignature(),
                authentication.getConsumerCredentials().getSignatureMethod(),
                authentication.getConsumerCredentials().getSignatureBaseString(),
                authentication.getConsumerCredentials().getToken()
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        log.info("createAuthentication generated LTI auth principal (" + principal + "): req=" + request);
        return auth;
    }

}
