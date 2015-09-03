/**
 * 
 */
package od.providers.api;

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.events.EventProvider;

import org.apereo.lai.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class EventController {
  private static final Logger log = LoggerFactory.getLogger(EventController.class);
  @Autowired private ProviderService providerService;

  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/event/course/{id}", method = RequestMethod.POST)
  public Page<Event> getEventsForCourse(@RequestBody ProviderOptions options, @RequestParam(value="page", required=false) int page,
      @RequestParam(value="size", required=false) int size)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    EventProvider eventProvider = providerService.getEventProvider(EventProvider.DEFAULT);

    return eventProvider.getEventsForCourse(options, new PageRequest(page, size));
  }

  @Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
  @RequestMapping(value = "/api/event/user/{id}", method = RequestMethod.POST)
  public Page<Event> getEventsForUser(@RequestBody ProviderOptions options, @RequestParam(value="page", required=false) int page,
      @RequestParam(value="size", required=false) int size)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    EventProvider eventProvider = providerService.getEventProvider(EventProvider.DEFAULT);

    return eventProvider.getEventsForUser(options, new PageRequest(page, size));
  }
  
  @Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
  @RequestMapping(value = "/api/event/course/{courseId}/user/{userId}", method = RequestMethod.POST)
  public Page<Event> getEventsForCourseAndUser(@RequestBody ProviderOptions options, @RequestParam(value="page", required=false) int page,
      @RequestParam(value="size", required=false) int size)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    EventProvider eventProvider = providerService.getEventProvider(EventProvider.DEFAULT);

    return eventProvider.getEventsForCourseAndUser(options, new PageRequest(page, size));
  }


}
