/**
 *
 */
package od;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.oauth.OAuthFilter;
import od.lti.LTIAuthenticationProvider;
import od.lti.LTIUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * @author ggilbert
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  
  @Configuration
  @Order(1)
  public static class LTIWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    @Autowired private OAuthFilter oAuthFilter;
    @Autowired private LTIUserDetailsService userDetailsService;
    @Autowired private LTIAuthenticationProvider authenticationProvider;
   
    @Bean
    public FilterRegistrationBean oAuthFilterBean() {
      FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(oAuthFilter);
      List<String> urls = new ArrayList<String>(1);
      urls.add("/lti");
      registrationBean.setUrlPatterns(urls);
      registrationBean.setOrder(2);
      return registrationBean;
    }

    protected void configure(HttpSecurity http) throws Exception {
      http
        .antMatcher("/lti")
          .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/lti").permitAll()
      .and()
        .headers().frameOptions().disable() 
        .csrf().disable();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {    
      auth
        .authenticationProvider(authenticationProvider)
        .userDetailsService(userDetailsService);
    }
    
    @Bean(name="LTIAuthenticationManager")
    public AuthenticationManager authManager() throws Exception {
      return super.authenticationManagerBean();
    }
  }
  
  @Configuration
  public static class HttpBasicConfigurationAdapter extends WebSecurityConfigurerAdapter {
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
          .antMatchers("/assets/**", "/favicon.ico", "/cards/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
      .httpBasic()
        .authenticationEntryPoint(new NoWWWAuthenticate401ResponseEntryPoint("opendashboard"))
      .and()
      .authorizeRequests()
        .antMatchers("/features/**", "/", "/login").permitAll()
        .anyRequest().authenticated()
      .and().csrf().csrfTokenRepository(csrfTokenRepository())
      .and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
        .inMemoryAuthentication()
          .withUser("student").password("student").roles("STUDENT")
          .and()
          .withUser("instructor").password("instructor").roles("INSTRUCTOR")
          .and()
          .withUser("admin").password("admin").roles("INSTRUCTOR","ADMIN");
    }
    
    @Primary
    @Bean
    public AuthenticationManager authManager() throws Exception {
      return super.authenticationManagerBean();
    }
    
    private Filter csrfHeaderFilter() {
      return new OncePerRequestFilter() {      
        @Override
        protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
          CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
          
          if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String token = csrf.getToken();
            if (cookie == null || token != null
                && !token.equals(cookie.getValue())) {
              cookie = new Cookie("XSRF-TOKEN", token);
              cookie.setPath("/");
              response.addCookie(cookie);
            }
          }
          filterChain.doFilter(request, response);
        }
      };
    }

    private CsrfTokenRepository csrfTokenRepository() {
      HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
      repository.setHeaderName("X-XSRF-TOKEN");
      return repository;
    }
    
    class NoWWWAuthenticate401ResponseEntryPoint extends BasicAuthenticationEntryPoint {
      
      public NoWWWAuthenticate401ResponseEntryPoint(String realm) {
        setRealmName(realm);
      }
      
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
          throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect("/login");
      }
    }

  }
  
  @Autowired
  private ExceptionFilter exceptionFilter;

  /*
   * Order of precedence
   * 
   * 1. exceptionFilter (handles exceptions thrown in downstream filters)
   * 2. oAuthFilter
   * 3. mongoFilterBean
   */
  
  @Bean
  public FilterRegistrationBean exceptionFilterBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(exceptionFilter);
    List<String> urls = new ArrayList<String>(1);
    urls.add("/");
    urls.add("/api/*");
    urls.add("/cm/*");
    registrationBean.setUrlPatterns(urls);
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
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
