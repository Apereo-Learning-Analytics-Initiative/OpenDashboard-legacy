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

import ltistarter.oauth.LTIConsumerDetailsService;
import ltistarter.oauth.LTIOAuthNonceServices;
import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ComponentScan("ltistarter")
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement // enables TX management and @Transaction
@EnableCaching // enables caching and @Cache* tags
@EnableWebSecurity // needed along with the one below for security
@EnableWebMvcSecurity // enable spring security and web mvc hooks
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
// allows @Secured flag - proxyTargetClass = true causes this to die
public class Application extends WebMvcConfigurerAdapter {

    final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Creates a CacheManager which allows the spring caching annotations to work
     * Annotations: Cacheable, CachePut and CacheEvict
     * http://spring.io/guides/gs/caching/
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(); // not appropriate for production, try JCacheCacheManager or HazelcastCacheManager instead
    }

    /**
     * Allows access to the H2 console at: {server}/console/
     * Enter this as the JDBC URL: jdbc:h2:mem:AZ
     */
    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/console/*");
        return registration;
    }

    // Spring Security

    @Autowired
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void configureSimpleAuthUsers(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN", "USER")
                .and().withUser("user").password("user").roles("USER");
    }

    @Configuration
    @Order(1) // HIGHEST
    public static class OAuthSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        ZeroLeggedOAuthProviderProcessingFilter oauthProviderProcessingFilter;
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // filters must be ordered: see http://docs.spring.io/spring-security/site/docs/3.2.0.RELEASE/apidocs/org/springframework/security/config/annotation/web/HttpSecurityBuilder.html#addFilter%28javax.servlet.Filter%29
            http.antMatcher("/oauth")
                    .addFilterBefore(oauthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().hasRole("OAUTH");
        }
    }

    @Order(23) // MED
    @Configuration
    public static class FormLoginConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/form").authorizeRequests().anyRequest().authenticated()
                    .and().formLogin().permitAll().loginPage("/form/login").loginProcessingUrl("/form/login")
                    .and().logout().logoutUrl("/form/logout").invalidateHttpSession(true).logoutSuccessUrl("/");
            //http.logout().permitAll().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true);
            //.and().logout().logoutUrl("/basic/logout").invalidateHttpSession(true).logoutSuccessUrl("/");
        }
    }

    @Order(45) // LOW
    @Configuration
    public static class BasicAuthConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/basic").authorizeRequests().anyRequest().authenticated()
                    .and().httpBasic();
        }
    }

    // OAuth beans

    @Autowired
    public LTIConsumerDetailsService oauthConsumerDetailsService;

    @Autowired
    public LTIOAuthNonceServices oauthNonceServices;

    @Autowired
    public OAuthAuthenticationHandler oauthAuthenticationHandler;

    public static class OAuthProcessingFilterEntryPointImpl extends OAuthProcessingFilterEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            log.info("OAuth FILTER Failure (commence), req=" + request + ", ex=" + authException);
            // Called when there is an OAuth Auth failure, authException may be InsufficientAuthenticationException
            super.commence(request, response, authException);
            /*
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON.getType());
            response.getWriter().println("{\"Unauthorized\":\"" + authException + "\"}");
            */
        }
    }

    @Bean(name = "oauthAuthenticationEntryPoint")
    public OAuthProcessingFilterEntryPoint oauthAuthenticationEntryPoint() {
        return new OAuthProcessingFilterEntryPointImpl();
    }

    @Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        // NOTE: we don't use the OAuthProviderTokenServices for 0-legged but it cannot be null
        return new InMemoryProviderTokenServices();
    }

    public static class ZeroLeggedOAuthProviderProcessingFilter extends ProtectedResourceProcessingFilter {
        ZeroLeggedOAuthProviderProcessingFilter(LTIConsumerDetailsService ltiConsumerDetailsService, LTIOAuthNonceServices ltioAuthNonceServices, OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint, OAuthAuthenticationHandler oAuthAuthenticationHandler, OAuthProviderTokenServices oauthProviderTokenServices) {
            super();
            log.info("CONSTRUCT Zero Legged OAuth provider");
            setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
            setAuthHandler(oAuthAuthenticationHandler);
            setConsumerDetailsService(ltiConsumerDetailsService);
            setNonceServices(ltioAuthNonceServices);
            setTokenServices(oauthProviderTokenServices);
            //setIgnoreMissingCredentials(false); // die if OAuth params are not included
        }
    }

    @Bean(name = "zeroLeggedOAuthProviderProcessingFilter")
    public ZeroLeggedOAuthProviderProcessingFilter oauthProviderProcessingFilter() {
        return new ZeroLeggedOAuthProviderProcessingFilter(oauthConsumerDetailsService, oauthNonceServices, oauthAuthenticationEntryPoint(), oauthAuthenticationHandler, oauthProviderTokenServices());
    }

}
