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
package od.providers.api;

import od.providers.ProviderOptions;
import od.providers.ProviderService;
import od.providers.events.EventProvider;
import od.repository.mongo.MongoTenantRepository;

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
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/event/course/{id}", method = RequestMethod.POST)
  public Page<Event> getEventsForCourse(@RequestBody ProviderOptions options, @RequestParam(value="page", required=false) int page,
      @RequestParam(value="size", required=false) int size)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("options " + options);
    }
    EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(options.getTenantId()));

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
    EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(options.getTenantId()));

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
    EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(options.getTenantId()));

    return eventProvider.getEventsForCourseAndUser(options, new PageRequest(page, size));
  }


}
