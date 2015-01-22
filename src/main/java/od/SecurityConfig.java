/**
 * 
 */
package od;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

import lti.oauth.OAuthFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
	@Autowired private OAuthFilter oAuthFilter;
	
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
		@Value("${auth.oauth.key}")
		private String securityKey;
		
		@Value("${auth.oauth.secret}")
		private String securitySecret;		
		
		@Autowired private LTIUserDetailsService userDetailsService;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/cards/**", "/css/**", "/framework/**", "/img/**", "/js/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // this ensures security context info (Principal, sec:authorize, etc.) is accessible on all paths
            http
            .headers()
            .frameOptions().disable()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .anyRequest().authenticated().and().csrf().disable();
        }
        
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//            auth
//            .inMemoryAuthentication()
//            .withUser(securityKey + "-Instructor").password(securitySecret).roles("INSTRUCTOR")
//            .and()
//            .withUser(securityKey + "-Student").password(securitySecret).roles("STUDENT");
            
            auth
            //.authenticationProvider(authenticationProvider)
            .userDetailsService(userDetailsService);
        }
    }
	
	@Bean
	public SessionTrackingConfigListener sessionTrackingConfigListener() {
		return new SessionTrackingConfigListener();
	}

	public class SessionTrackingConfigListener implements ServletContextInitializer {

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
			sessionCookieConfig.setName("ODSESSIONID");
		}

	}

}
