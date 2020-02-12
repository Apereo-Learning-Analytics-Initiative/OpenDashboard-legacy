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

import od.auth.OpenDashboardAuthenticationToken;
import od.framework.model.PulseDetail;
import od.providers.ProviderService;
import od.providers.events.EventProvider;
import od.repository.mongo.MongoTenantRepository;
import od.repository.mongo.PulseCacheRepository;
import od.utils.PulseUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apereo.lai.Event;
import org.apereo.lai.impl.EventImpl;
import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



/**
 * @author ggilbert
 *
 */
@RestController
public class EventController {
	private static final Logger log = LoggerFactory.getLogger(EventController.class);
	@Autowired
	private ProviderService providerService;
	@Autowired
	private MongoTenantRepository mongoTenantRepository;
	
	@Autowired private PulseCacheRepository pulseCacheRepository;

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN" })
	@RequestMapping(value = "/api/tenants/{tenantId}/classes/{classId}/events/stats", method = RequestMethod.GET)
	public ClassEventStatistics getEventStatisticsForClass(@PathVariable("tenantId") final String tenantId,
			@PathVariable("classId") final String classId,
			@RequestParam(name = "studentsOnly", required = false, defaultValue = "true") String studentsOnly)
			throws Exception {
		log.debug("tenantId: {}", tenantId);
		log.debug("classId: {}", classId);

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		return eventProvider.getStatisticsForClass(tenantId, classId, Boolean.valueOf(studentsOnly));
	}

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/classes/{classId}/events/{userId}", method = RequestMethod.GET)
	public List<Event> getEventsForClassAndUser(@PathVariable("classId") final String classId, @PathVariable("userId") final String userId
			) throws Exception {
		//log.debug("tenantId: {}", tenantId);
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);
		
		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		String tenantId = authentication.getTenantId();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		
		Page<Event> p = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 10000));
		return p.getContent();
	}

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN" })
	@RequestMapping(value = "/api/tenants/{tenantId}/event/course/{courseId}", method = RequestMethod.GET)
	public Page<Event> getEventsForCourse(@PathVariable("tenantId") final String tenantId,
			@PathVariable("courseId") final String courseId, @RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "size", required = false) int size) throws Exception {

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));

		return eventProvider.getEventsForCourse(tenantId, courseId, new PageRequest(page, size));
	}

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_STUDENT", "ROLE_ADMIN" })
	@RequestMapping(value = "/api/tenants/{tenantId}/event/user/{userId}", method = RequestMethod.GET)
	public Page<Event> getEventsForUser(@PathVariable("tenantId") final String tenantId,
			@PathVariable("userId") final String userId, @RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "size", required = false) int size) throws Exception {

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));

		return eventProvider.getEventsForUser(tenantId, userId, new PageRequest(page, size));
	}

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_STUDENT", "ROLE_ADMIN" })
	@RequestMapping(value = "/api/tenants/{tenantId}/event", method = RequestMethod.POST)
	public JsonNode postEvent(@RequestBody ObjectNode object, @PathVariable("tenantId") final String tenantId)
			throws Exception {
		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		return eventProvider.postEvent(object.get("caliperEvent"), tenantId);
	}
}
