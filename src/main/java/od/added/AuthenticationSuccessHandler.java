package od.added;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import od.auth.OpenDashboardAuthenticationToken;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
                HttpServletRequest request, 
                HttpServletResponse response, 
                Authentication authentication) throws IOException, ServletException {

        if( !(authentication instanceof OpenDashboardAuthenticationToken) ) {
            return;
        }

        OpenDashboardAuthenticationToken jwtAuthenticaton =    (OpenDashboardAuthenticationToken) authentication;

        // Add a session cookie
        //Cookie sessionCookie = new Cookie( "someSessionId", jwtAuthenticaton.getToken() );
        //response.addCookie( sessionCookie );

        //clearAuthenticationAttributes(request);

        // call the original impl
        super.onAuthenticationSuccess( request, response, authentication );
    }
}