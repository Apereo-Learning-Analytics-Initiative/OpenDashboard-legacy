///*******************************************************************************
// * Copyright 2015 Unicon (R) Licensed under the
// * Educational Community License, Version 2.0 (the "License"); you may
// * not use this file except in compliance with the License. You may
// * obtain a copy of the License at
// *
// * http://www.osedu.org/licenses/ECL-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an "AS IS"
// * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// * or implied. See the License for the specific language governing
// * permissions and limitations under the License.
// *******************************************************************************/
///**
// *
// */
//package od;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.SessionCookieConfig;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import lti.oauth.OAuthFilter;
//import od.added.AuthenticationSuccessHandler;
//import od.added.CookieAuthenticationFilter;
//import od.added.JwtTokenUtil;
//import od.auth.OpenDashboardAuthenticationProvider;
//import od.auth.OpenDashboardUserDetailsService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Required;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletContextInitializer;
////import org.springframework.boot.context.embedded.FilterRegistrationBean;
////import org.springframework.boot.context.embedded.ServletContextInitializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
//import org.springframework.security.web.session.SessionManagementFilter;
//import org.springframework.security.web.util.matcher.AndRequestMatcher;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.WebUtils;
//
///**
// * @author ggilbert
// *
// */
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
//public class SecurityConfig {
//
//	@Configuration
//	@Order(1)
//	public static class LTIWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
//		@Autowired
//		private OAuthFilter oAuthFilter;
//
//		@Bean
//		public FilterRegistrationBean oAuthFilterBean() {
//			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//			registrationBean.setFilter(oAuthFilter);
//			List<String> urls = new ArrayList<>(1);
//			urls.add("/lti");
//			registrationBean.setUrlPatterns(urls);
//			registrationBean.setOrder(2);
//			return registrationBean;
//		}
//
//		protected void configure(HttpSecurity http) throws Exception {
//			http.cors().and().antMatcher("/lti").authorizeRequests().antMatchers(HttpMethod.POST, "/lti").permitAll()
//					.and().headers()
//					.contentSecurityPolicy(
//							"script-src 'self' 'unsafe-eval' https://cdnjs.cloudflare.com https://ajax.googleapis.com https://maxcdn.bootstrapcdn.com https://www.google.com")
//					.and().frameOptions().disable().and().csrf().disable();
//		}
//	}
//
//	@Configuration
//	public static class HttpBasicConfigurationAdapter extends WebSecurityConfigurerAdapter {
//		@Autowired
//		private OpenDashboardUserDetailsService userDetailsService;
//		@Autowired
//		private OpenDashboardAuthenticationProvider authenticationProvider;
//
//		@Bean
//		public FilterRegistrationBean oAuthFilterBean() {
//			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//			registrationBean.setFilter(
//					getCookieAuthenticationFilter(new AndRequestMatcher(new AntPathRequestMatcher("/api/**"))));
//			List<String> urls = new ArrayList<>(1);
//			urls.add("/api/**");
//			registrationBean.setUrlPatterns(urls);
//			registrationBean.setOrder(2);
//			return registrationBean;
//		}
//
//		@Override
//		public void configure(WebSecurity web) throws Exception {
//			web.ignoring().antMatchers("/assets/**", "/favicon.ico", "/cards/**", "/tenant/**", "/jwtlogin/**");
//		}
//
//		@Autowired
//		AuthenticationSuccessHandler authSuccessHandler;
//
//	@Override
//    protected void configure(HttpSecurity http) throws Exception {
//      http      
//      .httpBasic()
//        .authenticationEntryPoint(new NoWWWAuthenticate401ResponseEntryPoint("opendashboard"))
//      .and()
//      .cors().and()
//      .authorizeRequests()
//        .antMatchers("/features/**", "/", "/login", "/err/**").permitAll()
//        .antMatchers("/api/tenants/{tenantId}/**").access("@methodSecurity.checkTenant(authentication,#tenantId)")  
//        
//        .anyRequest().authenticated()
//      .and()
//        .logout()
//          .invalidateHttpSession(true)
//          .deleteCookies("ODSESSIONID", "X-OD-TENANT")
//      .and()
//        .headers()
//          .contentSecurityPolicy("script-src 'self' 'unsafe-eval' https://cdnjs.cloudflare.com https://ajax.googleapis.com https://maxcdn.bootstrapcdn.com https://www.google.com")
//          .and()
//          .frameOptions().disable()
//      .and()
//        .csrf().csrfTokenRepository(csrfTokenRepository())
//      /**
//       * 
//       * TODO revisit after updating to Spring Security 4.1 
//       * Currently the SessionManagementFilter is added here instead of the CsrfFilter 
//       * Two session tokens are generated, one token is created before login and one token is created after.
//       * The Csrf doesn't update with the second token.
//       * Logout does not work as a side effect.
//       * @link https://github.com/dsyer/spring-security-angular/issues/15
//       * 
//       * */
//      .and().addFilterAfter(csrfHeaderFilter(), SessionManagementFilter.class)
//      
//      // JWT cookie filter
//      .addFilterAfter( cookieAuthenticationFilter )
//      ) , UsernamePasswordAuthenticationFilter.class );
//    }
//
//		@Bean
//		CookieAuthenticationFilter getCookieAuthenticationFilter() {
//
//			return new CookieAuthenticationFilter();
//		}
//
//		@Autowired
//		SimpleUrlAuthenticationFailureHandler authFailureHandler;
//
//		@Autowired
//		JwtTokenUtil jwtTokenUtil;
//
//		@Override
//		public void configure(AuthenticationManagerBuilder auth) throws Exception {
//			auth.authenticationProvider(authenticationProvider).userDetailsService(userDetailsService);
//		}
//
//		@Primary
//		@Bean
//		public AuthenticationManager authManager() throws Exception {
//			return super.authenticationManagerBean();
//		}
//
//		private Filter csrfHeaderFilter() {
//			return new OncePerRequestFilter() {
//				@Override
//				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//						FilterChain filterChain) throws ServletException, IOException {
//					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//
//					if (csrf != null) {
//						Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
//						String token = csrf.getToken();
//						if (cookie == null || token != null && !token.equals(cookie.getValue())) {
//							cookie = new Cookie("XSRF-TOKEN", token);
//							cookie.setPath("/");
//							response.addCookie(cookie);
//						}
//					}
//					filterChain.doFilter(request, response);
//				}
//			};
//		}
//
//		private CsrfTokenRepository csrfTokenRepository() {
//			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//			repository.setHeaderName("X-XSRF-TOKEN");
//			return repository;
//		}
//
//		static class NoWWWAuthenticate401ResponseEntryPoint extends BasicAuthenticationEntryPoint {
//
//			public NoWWWAuthenticate401ResponseEntryPoint(String realm) {
//				setRealmName(realm);
//			}
//
//			@Override
//			public void commence(HttpServletRequest request, HttpServletResponse response,
//					AuthenticationException authException) throws IOException, ServletException {
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//				response.sendRedirect("/login");
//			}
//		}
//
//	}
//
//	@Autowired
//	private ExceptionFilter exceptionFilter;
//
//	/*
//	 * Order of precedence
//	 * 
//	 * 1. exceptionFilter (handles exceptions thrown in downstream filters) 2.
//	 * oAuthFilter 3. mongoFilterBean
//	 */
//
//	@Bean
//	public FilterRegistrationBean exceptionFilterBean() {
//		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//		registrationBean.setFilter(exceptionFilter);
//		List<String> urls = new ArrayList<>(1);
//		urls.add("/");
//		urls.add("/api/*");
//		urls.add("/cm/*");
//		registrationBean.setUrlPatterns(urls);
//		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//		return registrationBean;
//	}
//
//	@Bean
//	public SessionTrackingConfigListener sessionTrackingConfigListener() {
//		return new SessionTrackingConfigListener();
//	}
//
//	public static class SessionTrackingConfigListener implements ServletContextInitializer {
//
//		@Override
//		public void onStartup(ServletContext servletContext) throws ServletException {
//			SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
//			sessionCookieConfig.setName("ODSESSIONID");
//		}
//
//	}
//
//}
