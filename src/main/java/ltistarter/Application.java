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

import org.h2.server.web.WebServlet;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@ComponentScan("ltistarter")
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement // enables TX management and @Transaction
@EnableCaching // enables caching and @Cache* tags
@EnableWebMvcSecurity // enable spring security and web mvc hooks
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // allows @Secured flag
public class Application extends WebMvcConfigurerAdapter {

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

    @Configuration
    @Order(1) // HIGHEST
    public static class OAuthSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/oauth").authorizeRequests().anyRequest().denyAll();
        }
    }

    @Order(23) // MED
    @Configuration
    public static class FormLoginConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/form").authorizeRequests().anyRequest().authenticated().and().formLogin().permitAll().loginPage("/login");
            //http.logout().permitAll().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true);
            //.and().logout().logoutUrl("/basic/logout").invalidateHttpSession(true).logoutSuccessUrl("/");
        }
    }

    @Order(45) // LOW
    @Configuration
    public static class BasicAuthConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/basic").authorizeRequests().anyRequest().authenticated().and().httpBasic();
        }
    }

    @Autowired
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void configureSimpleAuthUsers(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN", "USER")
                .and().withUser("user").password("user").roles("USER");
    }

    /*
    @Bean
    public OAuthAuthenticationHandler authenticationHandler() {
        return new DefaultAuthenticationHandler();
        // OAuthProcessingFilterEntryPoint?
    }*/

    /*
    extends WebSecurityConfigurerAdapter {
@EnableWebMvc
@EnableWebMvcSecurity // enable spring security and web mvc hooks
@EnableGlobalMethodSecurity(prePostEnabled = true)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 0-Legged OAuth on the /oauth and /lti paths only
        http.requestMatchers().antMatchers("/oauth"); // .and().... what?
        // ??? something must be missing here - provider?
    }
    */

}
