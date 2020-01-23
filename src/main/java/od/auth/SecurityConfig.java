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
package od.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
//import org.springframework.boot.context.embedded.FilterRegistrationBean;
//import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import lti.oauth.OAuthFilter;
import od.added.RestAuthenticationEntryPoint;

/**
 * @author ggilbert
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
		return new SecurityContextHolderAwareRequestFilter();
	}

	@Configuration
	@Order(1)
	public static class LTIWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private OAuthFilter oAuthFilter;
		
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/assets/**", "/favicon.ico", "/cards/**", "/tenant/**", "/jwtlogin/**");
		}
		
		@Bean
		public FilterRegistrationBean oAuthFilterBean() {
			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
			registrationBean.setFilter(oAuthFilter);
			List<String> urls = new ArrayList<>(1);
			urls.add("/lti");
			urls.add("/login");
			registrationBean.setUrlPatterns(urls);
			registrationBean.setOrder(2);
			return registrationBean;
		}
		
		//TODO: add the oauth filter here
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and()
			.requestMatchers().antMatchers("/lti","/login").and().authorizeRequests().
			    antMatchers(HttpMethod.POST, "/lti").permitAll()
					.and().headers()
					.contentSecurityPolicy(
							"script-src 'self' 'unsafe-eval' https://cdnjs.cloudflare.com https://ajax.googleapis.com https://maxcdn.bootstrapcdn.com https://www.google.com")
					.and().frameOptions().disable().and().csrf().disable();
		}
	}
	
	

	
	

	@Configuration
	@Order(2)
	public static class HttpBasicConfigurationAdapter extends WebSecurityConfigurerAdapter {
		
	    @Autowired private OpenDashboardAuthenticationProvider authenticationProvider;
	    
	    @Autowired private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

		@Autowired
		private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

		@Autowired
		private JwtRequestFilter jwtRequestFilter;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/assets/**", "/favicon.ico", "/cards/**", "/tenant/**", "/jwtlogin/**");
		}
		
		
		@Override
		protected void configure(HttpSecurity httpSecurity) throws Exception {
			httpSecurity.csrf().disable()
			.cors().and()
				.authorizeRequests()
					.antMatchers("/modules").permitAll()
					// all other requests need to be authenticated
					.anyRequest().authenticated().and()
					.exceptionHandling()
					//.authenticationEntryPoint(jwtAuthenticationEntryPoint)
					.authenticationEntryPoint(restAuthenticationEntryPoint)
					.and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().formLogin();
			
			//httpSecurity.authenticationProvider(authenticationProvider);
			// Add a filter to validate the tokens with every request
			httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		}
		

		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		    auth.inMemoryAuthentication()
		        .withUser("admin").password(encoder().encode("admin")).roles("ADMIN")
		        .and()
		        .withUser("user").password(encoder().encode("user")).roles("USER");
		}
		
		@Bean
		public PasswordEncoder  encoder() {
		    return new BCryptPasswordEncoder();
		}

	}
}
