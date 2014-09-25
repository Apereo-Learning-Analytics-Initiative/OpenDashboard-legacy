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

import javax.annotation.PostConstruct;

import ltistarter.lti.LTIConsumerDetailsService;
import ltistarter.lti.LTIDataService;
import ltistarter.lti.LTIOAuthAuthenticationHandler;
import ltistarter.lti.LTIOAuthProviderProcessingFilter;
import ltistarter.oauth.MyConsumerDetailsService;
import ltistarter.oauth.MyOAuthAuthenticationHandler;
import ltistarter.oauth.MyOAuthNonceServices;
import ltistarter.oauth.ZeroLeggedOAuthProviderProcessingFilter;

import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringValueResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@ComponentScan("ltistarter")
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement // enables TX management and @Transaction
@EnableCaching // enables caching and @Cache* tags
@EnableWebMvcSecurity // enable spring security and web mvc hooks
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
// allows @Secured flag - proxyTargetClass = true causes this to die
public class Application extends WebMvcConfigurerAdapter {

    final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Allows access to the various config values (from application.properties) using @Value
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer() {
            @Override
            protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
                log.info("doProcessProperties");
                super.doProcessProperties(beanFactoryToProcess, valueResolver);
            }
        };
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
    public static class LTISecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private LTIOAuthProviderProcessingFilter ltioAuthProviderProcessingFilter;
        @Autowired
        LTIDataService ltiDataService;
        @Autowired
        LTIConsumerDetailsService oauthConsumerDetailsService;
        @Autowired
        MyOAuthNonceServices oauthNonceServices;
        @Autowired
        LTIOAuthAuthenticationHandler oauthAuthenticationHandler;
        @Autowired
        OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
        @Autowired
        OAuthProviderTokenServices oauthProviderTokenServices;

        @PostConstruct
        public void init() {
            ltioAuthProviderProcessingFilter = new LTIOAuthProviderProcessingFilter(ltiDataService, oauthConsumerDetailsService, oauthNonceServices, oauthProcessingFilterEntryPoint, oauthAuthenticationHandler, oauthProviderTokenServices);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            /**/
            http.requestMatchers().antMatchers("/lti/**")
                .and().addFilterAfter(ltioAuthProviderProcessingFilter, LogoutFilter.class)
                .authorizeRequests().anyRequest().hasRole("LTI")
                .and().csrf().disable();
            http.headers().frameOptions().disable();
            /*
            http.antMatcher("/lti/**")
                    .addFilterBefore(ltioAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().hasRole("LTI")
                    .and().csrf().disable(); // probably need https://github.com/spring-projects/spring-boot/issues/179
            /**/
        }

//        @Override
//        protected void configure(AuthenticationManagerBuilder authManagerBuilder)
//                throws Exception {
//                   authManagerBuilder.authenticationProvider(this());
//        }
    }

    @Configuration
    @Order(11) // HIGH
    public static class OAuthSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private ZeroLeggedOAuthProviderProcessingFilter zeroLeggedOAuthProviderProcessingFilter;
        @Autowired
        MyConsumerDetailsService oauthConsumerDetailsService;
        @Autowired
        MyOAuthNonceServices oauthNonceServices;
        @Autowired
        MyOAuthAuthenticationHandler oauthAuthenticationHandler;
        @Autowired
        OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
        @Autowired
        OAuthProviderTokenServices oauthProviderTokenServices;

        @PostConstruct
        public void init() {
            // NOTE: have to build the filter here: http://stackoverflow.com/questions/24761194/how-do-i-stop-spring-filterregistrationbean-from-registering-my-filter-on/24762970
            zeroLeggedOAuthProviderProcessingFilter = new ZeroLeggedOAuthProviderProcessingFilter(oauthConsumerDetailsService, oauthNonceServices, oauthProcessingFilterEntryPoint, oauthAuthenticationHandler, oauthProviderTokenServices, false);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/oauth/**")
                    // added filters must be ordered: see http://docs.spring.io/spring-security/site/docs/3.2.0.RELEASE/apidocs/org/springframework/security/config/annotation/web/HttpSecurityBuilder.html#addFilter%28javax.servlet.Filter%29
                    .addFilterBefore(zeroLeggedOAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().hasRole("OAUTH")
                    .and().csrf().disable(); // see above
        }
    }

    @Order(23) // MED
    @Configuration
    public static class FormLoginConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/form/**").authorizeRequests().anyRequest().authenticated()
                    .and().formLogin().permitAll().loginPage("/form/login").loginProcessingUrl("/form/login")
                    .and().logout().logoutUrl("/form/logout").invalidateHttpSession(true).logoutSuccessUrl("/");
        }
    }

    @Order(45) // LOW
    @Configuration
    public static class BasicAuthConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // basic auth protection for the /basic path
            http.antMatcher("/basic/**").authorizeRequests().anyRequest().authenticated()
                    .and().httpBasic();
        }
    }

    @Order(67) // LOWEST
    @Configuration
    public static class NoAuthConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/console/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // this ensures security context info (Principal, sec:authorize, etc.) is accessible on all paths
            http.antMatcher("/**").authorizeRequests().anyRequest().permitAll().and().csrf().disable();
            // TEMPORARY
            http
            .headers()
            .cacheControl()
            .contentTypeOptions()
            .xssProtection()
            .frameOptions().disable();
        }
    }

    // OAuth beans

    @Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        // NOTE: we don't use the OAuthProviderTokenServices for 0-legged but it cannot be null
        return new InMemoryProviderTokenServices();
    }

}
