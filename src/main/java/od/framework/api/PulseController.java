package od.framework.api;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PulseController {
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR"})
  @RequestMapping(value = "/api/pulse", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public void pulse() {
    
  }
  
}
