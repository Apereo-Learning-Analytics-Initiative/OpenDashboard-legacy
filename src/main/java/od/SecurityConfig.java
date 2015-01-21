/**
 * 
 */
package od;

import java.util.ArrayList;
import java.util.List;

import lti.oauth.OAuthFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author ggilbert
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired private APIFilter apiFilter;
    @Autowired private OAuthFilter oAuthFilter;
    
    @Bean
    public FilterRegistrationBean apiFilterBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(apiFilter);
        List<String> urls = new ArrayList<String>(1);
        urls.add("/api/*");
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(1);
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean oAuthFilterBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(oAuthFilter);
        List<String> urls = new ArrayList<String>(1);
        urls.add("/");
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(2);
        return registrationBean;
    }

    
    @Order(99)
    @Configuration
    public static class NoAuthConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // this ensures security context info (Principal, sec:authorize, etc.) is accessible on all paths
            http.antMatcher("/**").authorizeRequests().anyRequest().permitAll().and().csrf().disable();
        }
    }
}
