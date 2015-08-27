/**
 * 
 */
package od.api;

import java.util.List;

import od.assignment.Assignment;
import od.assignment.AssignmentsController;
import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.modeloutput.ModelOutputProvider;

import org.apereo.lai.ModelOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class ModelOutputController {
  private static final Logger log = LoggerFactory.getLogger(ModelOutputController.class);
  @Autowired private ProviderService providerService;
  
  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/modeloutput/course/{courseId}", method = RequestMethod.POST)
  public List<ModelOutput> modelOutputForCourse(@RequestBody ProviderOptions options)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    
    ModelOutputProvider modelOutputProvider = providerService.getModelOutputProvider(ModelOutputProvider.DEFAULT);
    
    return modelOutputProvider.getModelOutputForCourse(options, null, null);
  }

}
