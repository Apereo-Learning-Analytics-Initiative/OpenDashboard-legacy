package od.added;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import od.auth.JwtTokenUtil;

@Component
public final class RestAuthenticationEntryPoint 
  implements AuthenticationEntryPoint {

	  @Autowired private JwtTokenUtil jwtTokenUtil;
	
    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) throws IOException {
         
        //response.setContentType("application/json");
        //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //response.getOutputStream().println("{ \"error\": \"" + "test" + "\" }");

    	
    	//handle username and password here

    	
    	//Set the JWT Token here
       /* Map<String, Object> claims = new HashMap<>();
        List<String> myroles = new ArrayList<String>();
        myroles.add("ROLE_ADMIN");

        final String jwtToken = jwtTokenUtil.generateToken(claims);
        claims.put("Authorities", myroles);
        
        Cookie cookie = new Cookie("ttttsecurityToken", jwtToken);    
        response.addCookie(cookie);*/
    }
}