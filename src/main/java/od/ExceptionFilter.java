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
package od;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import od.exception.MissingCookieException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author ggilbert
 *
 */
@Component
public class ExceptionFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
    logger.debug("In ExceptionFilter");
    
    try {
      fc.doFilter(req, res);
      
      // handle exceptions thrown by downstream filters
      // catch exception type / set appropriate http status code
      // and error message key
      // ultimately these responses are handled by 
      // https://github.com/spring-projects/spring-boot/blob/master/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/BasicErrorController.java
      // the response varies based on the content type of the request (i.e., text/html will return the /error.html page, otherwise a json response is returned)
    }
    catch (MissingCookieException missingCookieException) {
      // getting a little creative with the status code here
      // want to distiguish from unauthorized
      res.sendError(HttpStatus.NOT_ACCEPTABLE.value(), "COOKIE_ERROR");
    }
    catch (AuthorizationServiceException authorizationServiceException) {
      res.sendError(HttpStatus.UNAUTHORIZED.value(), "AUTHORIZATION_ERROR");
    }
    catch(Throwable t) {
      res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "GENERAL_ERROR");
    }
    
  }

}
