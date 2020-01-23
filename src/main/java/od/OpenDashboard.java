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
package od;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.RedirectView;

@ComponentScan(basePackages = { "od", "lti" })
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
public class OpenDashboard {
	


  final static Logger log = LoggerFactory.getLogger(OpenDashboard.class);
  
  public static void main(String[] args) {
    SpringApplication.run(OpenDashboard.class, args);
  }
  
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.US);
    return slr;
  }
  
  @Bean
  public TaskScheduler taskScheduler() {
      return new ConcurrentTaskScheduler(); //single threaded by default
  }
  
 
  @Controller
  public static class OpenDashboardController {
    @Secured({ "ROLE_ADMIN", "ROLE_INSTRUCTOR", "ROLE_STUDENT" })
    @RequestMapping(value = { "/cm/**", "/welcome", "/direct/**" }, method = RequestMethod.GET)
    public String secureRoutes() {
      return "index";
    }
    
    @RequestMapping(value = { "/", "/logintt", "/err/**" }, method = RequestMethod.GET)
    public String openRoutes() {
      return "index";
    }
    
    
	  @Value("${opendashboard.uxbaseurl}")
	  private String uxBaseUrl;
     
    @RequestMapping(value = { "/loginaaa" }, method = RequestMethod.GET)
    public RedirectView openLoginRoutes() {
      return new RedirectView(uxBaseUrl + "/admin/tenants");
    }

  }
  
  @RestController
  public static class AuthenticatedCheckController {
    @RequestMapping("/user")
    public Principal user(Principal user) {
      
      log.debug(user.toString());
      log.debug(user.getClass().getName());
      
      return user;
    }
  }
  
  @RestController
  public static class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Autowired private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring. 
        // Here we just define response body.
        return new ErrorJson(response.getStatus(), getErrorAttributes(request, false));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
    
    static class ErrorJson {

      public Integer status;
      public String error;
      public String message;
      public String timeStamp;
      public String trace;

      public ErrorJson(int status, Map<String, Object> errorAttributes) {
          this.status = status;
          this.error = (String) errorAttributes.get("error");
          this.message = (String) errorAttributes.get("message");
          this.timeStamp = errorAttributes.get("timestamp").toString();
          this.trace = (String) errorAttributes.get("trace");
      }
  }
    
  }
  
  @Bean
  public Executor taskExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(5);
      executor.setMaxPoolSize(10);
      executor.setQueueCapacity(150000);
      executor.setThreadNamePrefix("taskExecutor-");
      executor.initialize();
      return executor;
  }

}
