package od.added;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lti.LaunchRequest;
import od.auth.OpenDashboardAuthenticationToken;

@Component
public class CookieAuthenticationFilter extends BasicAuthenticationFilter {

		private JwtTokenUtil jwtTokenUtil;
		
		
		/*
        public CookieAuthenticationFilter( RequestMatcher requestMatcher, JwtTokenUtil jwtTokenUtil ) {

            super( requestMatcher );
            setAuthenticationManager( super.getAuthenticationManager() );
            this.jwtTokenUtil = jwtTokenUtil;
        }*/
        
        CookieAuthenticationFilter(AuthenticationManager authenticationManager) {
            super(authenticationManager);
        }
/*
        @Override
        public Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response )
            throws AuthenticationException, IOException, ServletException {

            String token = "";

            // get token from a Cookie
            Cookie[] cookies = request.getCookies();

            if( cookies == null || cookies.length < 1 ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }

            Cookie sessionCookie = null;
            for( Cookie cookie : cookies ) {
                if( ( "securityToken").equals( cookie.getName() ) ) {
                sessionCookie = cookie;
                break;
                }
            }

            // TODO: move the cookie validation into a private method
            if( sessionCookie == null || StringUtils.isEmpty( sessionCookie.getValue() ) ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }
            
            String jwtToken = sessionCookie.getValue();
            Map<String, Object> claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
            
            OpenDashboardAuthenticationToken oda = new OpenDashboardAuthenticationToken(
            		LaunchRequest.fromJSON(claims.get("launchRequest").toString()), 
            		jwtToken, 
            		claims.get("tenantId").toString(), 
            		claims.get("userSourcedId"), "", null);
            
            SecurityContextHolder.getContext()
            .setAuthentication(oda);
                        
            //oda.setAuthenticated(true);
           // JWTAuthenticationToken jwtAuthentication = new JWTAuthenticationToken( sessionCookie.getValue(), null, null );

            return oda;

        }*/

/*
        @Override
        public void doFilter(HttpServletRequest req, HttpServletResponse res,
                 FilterChain chain) throws IOException, ServletException {
        	
        	String token = "";

            // get token from a Cookie
            Cookie[] cookies = req.getCookies();

            if( cookies == null || cookies.length < 1 ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }

            Cookie sessionCookie = null;
            for( Cookie cookie : cookies ) {
                if( ( "securityToken").equals( cookie.getName() ) ) {
                sessionCookie = cookie;
                break;
                }
            }

            // TODO: move the cookie validation into a private method
            if( sessionCookie == null || StringUtils.isEmpty( sessionCookie.getValue() ) ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }
            
            String jwtToken = sessionCookie.getValue();
            Map<String, Object> claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
            
            OpenDashboardAuthenticationToken oda = new OpenDashboardAuthenticationToken(
            		LaunchRequest.fromJSON(claims.get("launchRequest").toString()), 
            		jwtToken, 
            		claims.get("tenantId").toString(), 
            		claims.get("userSourcedId"), "", null);
            
            SecurityContextHolder.getContext()
            .setAuthentication(oda);
        	
            super.doFilter(req, res, chain);
        }
*/
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
        	String token = "";

            // get token from a Cookie
            Cookie[] cookies = request.getCookies();

            if( cookies == null || cookies.length < 1 ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }

            Cookie sessionCookie = null;
            for( Cookie cookie : cookies ) {
                if( ( "securityToken").equals( cookie.getName() ) ) {
                sessionCookie = cookie;
                break;
                }
            }

            // TODO: move the cookie validation into a private method
            if( sessionCookie == null || StringUtils.isEmpty( sessionCookie.getValue() ) ) {
                throw new AuthenticationServiceException( "Invalid Token" );
            }
            
            String jwtToken = sessionCookie.getValue();
            Map<String, Object> claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
            
            OpenDashboardAuthenticationToken oda = new OpenDashboardAuthenticationToken(
            		LaunchRequest.fromJSON(claims.get("launchRequest").toString()), 
            		jwtToken, 
            		claims.get("tenantId").toString(), 
            		claims.get("userSourcedId"), "", null);
            
            SecurityContextHolder.getContext()
            .setAuthentication(oda);
            
            
            chain.doFilter(request, response);
        }
}