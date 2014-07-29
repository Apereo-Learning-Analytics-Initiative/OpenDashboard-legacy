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
package ltistarter;

import ltistarter.config.ApplicationConfig;
import ltistarter.oauth.MyOAuthAuthenticationHandler;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("testing") // make the active profile "testing"
public abstract class BaseApplicationTest {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public ApplicationConfig applicationConfig;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public ConfigurableWebApplicationContext context;

    @PostConstruct
    public void init() {
        applicationConfig.getEnvironment().setActiveProfiles("testing");
    }

    @Test
    public void checkSpring() {
        assertNotNull(context);
        assertNotNull(applicationConfig);
        assertTrue(applicationConfig.getEnvironment().acceptsProfiles("testing"));
    }

    /**
     * Makes a new session which contains authentication roles,
     * this allows us to test requests with varying types of security
     *
     * @param username the username to set for the session
     * @param roles    all the roles to grant for this session
     * @return the session object to pass to mockMvc (e.g. mockMvc.perform(get("/").session(session))
     */
    public MockHttpSession makeAuthSession(String username, String... roles) {
        if (StringUtils.isEmpty(username)) {
            username = "azeckoski";
        }
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        Collection<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null && roles.length > 0) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        //Authentication authToken = new UsernamePasswordAuthenticationToken("azeckoski", "password", authorities); // causes a NPE when it tries to access the Principal
        Principal principal = new MyOAuthAuthenticationHandler.NamedOAuthPrincipal(username, authorities,
                "key", "signature", "HMAC-SHA-1", "signaturebase", "token");
        Authentication authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return session;
    }

}