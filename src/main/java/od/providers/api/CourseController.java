/**
 * 
 */
package od.providers.api;

import java.util.List;

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.course.CourseProvider;

import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
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
public class CourseController {
  private static final Logger log = LoggerFactory.getLogger(CourseController.class);
  
  @Autowired private ProviderService providerService;
  
  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/context", method = RequestMethod.POST)
  public List<CourseImpl> contexts(@RequestBody ProviderOptions options)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    CourseProvider courseProvider = providerService.getCourseProvider(CourseProvider.DEFAULT);

    return courseProvider.getContexts(options);
  }

  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/context/{id}", method = RequestMethod.POST)
  public Course context(@RequestBody ProviderOptions options)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    CourseProvider courseProvider = providerService.getCourseProvider(CourseProvider.DEFAULT);

    return courseProvider.getContext(options);
  }

}
