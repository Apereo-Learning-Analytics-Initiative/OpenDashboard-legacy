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
	
	
	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/eventsbycount/{classId}/{userId}", method = RequestMethod.GET)
	public Module1 getEventsByCountClassAndUserAdded(@PathVariable("classId") final String classId,
			@PathVariable("userId") final String userId, @RequestParam(required = false) List<String> excludeEvents,
			@RequestParam(name = "start", required = false) String startDate,
			@RequestParam(name = "end", required = false) String endDate) throws Exception {
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);

		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);
		String tenantId = authentication.getTenantId();

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		Page<Event> pagedEvents = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 1000));

		// convert Events to EventImpls
		List<EventImpl> eventImpls = new ArrayList<>();
		for (Event event : pagedEvents.getContent()) {
			eventImpls.add((EventImpl) event);
			EventImpl eventImpl = (EventImpl) event;
		}

		Date start;
		Date end;
		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
			eventImpls = eventImpls.stream().filter(s -> s.getDate().before(end)).filter(s -> s.getDate().after(start))
					.collect(Collectors.toList());
		}

		// filter out before
		// filter out after
		// filter out events
		// if(excludeEvents != null && excludeEvents.size()>0) {

		// }

		// for now
		Module1 module1 = getEventsByCount(eventImpls);

		ObjectMapper Obj = new ObjectMapper();

		try {

			// get Oraganisation object as a json string
			String jsonStr = Obj.writeValueAsString(module1);
			System.out.println("module1: " + jsonStr);

		} catch (Exception e) {
		}

		return module1;
	}	
	
	
	
	
	
	
	

	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/eventsbydate/{classId}/{userId}", method = RequestMethod.GET)
	public Module1 getEventsForClassAndUserAdded(@PathVariable("classId") final String classId,
			@PathVariable("userId") final String userId, @RequestParam(required = false) List<String> excludeEvents,
			@RequestParam(name = "start", required = false) String startDate,
			@RequestParam(name = "end", required = false) String endDate) throws Exception {
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);

		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);
		String tenantId = authentication.getTenantId();

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		Page<Event> pagedEvents = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 1000));

		// convert Events to EventImpls
		List<EventImpl> eventImpls = new ArrayList<>();
		for (Event event : pagedEvents.getContent()) {
			eventImpls.add((EventImpl) event);
			EventImpl eventImpl = (EventImpl) event;
		}		
		
		
		
		
		Date start;
		Date end;
		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
			eventImpls = eventImpls.stream().filter(s -> s.getDate().before(end)).filter(s -> s.getDate().after(start))
					.collect(Collectors.toList());
		}
		

		

		// filter out before
		// filter out after
		// filter out events
		// if(excludeEvents != null && excludeEvents.size()>0) {

		// }

		// for now
		Module1 module1 = getEventsByDateDay(eventImpls);

		ObjectMapper Obj = new ObjectMapper();

		try {

			// get Oraganisation object as a json string
			String jsonStr = Obj.writeValueAsString(eventImpls);
			System.out.println("EventImpls: " + jsonStr);

		} catch (Exception e) {
		}

		return module1;
	}
	
	
	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/eventsgroupedbydaydoughnut/{classId}/{userId}", method = RequestMethod.GET)
	public DoughnutModule getEventsForClassGroupedByDayDoughnut(@PathVariable("classId") final String classId,
			@PathVariable("userId") final String userId, @RequestParam(required = false) List<String> excludeEvents,
			@RequestParam(name = "start", required = false) String startDate,
			@RequestParam(name = "end", required = false) String endDate) throws Exception {
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);

		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);
		String tenantId = authentication.getTenantId();

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		Page<Event> pagedEvents = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 1000));

		// convert Events to EventImpls
		List<EventImpl> eventImpls = new ArrayList<>();
		for (Event event : pagedEvents.getContent()) {
			eventImpls.add((EventImpl) event);
		}

		Date start;
		Date end;
		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
			eventImpls = eventImpls.stream().filter(s -> s.getDate().before(end)).filter(s -> s.getDate().after(start))
					.collect(Collectors.toList());
		}

		// filter out before
		// filter out after
		// filter out events
		// if(excludeEvents != null && excludeEvents.size()>0) {

		// }

		// for now
		DoughnutModule doughnutModule = getEventSumsByDayOfWeekAccumDoughnut(eventImpls);

		ObjectMapper Obj = new ObjectMapper();

		try {

			// get Oraganisation object as a json string
			String jsonStr = Obj.writeValueAsString(doughnutModule);
			System.out.println("module1: " + jsonStr);

		} catch (Exception e) {
		}

		return doughnutModule;
	}	
	
	
	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/eventsgroupedbyday/{classId}/{userId}", method = RequestMethod.GET)
	public Module1 getEventsForClassGroupedByDay(@PathVariable("classId") final String classId,
			@PathVariable("userId") final String userId, @RequestParam(required = false) List<String> excludeEvents,
			@RequestParam(name = "start", required = false) String startDate,
			@RequestParam(name = "end", required = false) String endDate) throws Exception {
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);

		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);
		String tenantId = authentication.getTenantId();

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		Page<Event> pagedEvents = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 1000));

		// convert Events to EventImpls
		List<EventImpl> eventImpls = new ArrayList<>();
		for (Event event : pagedEvents.getContent()) {
			eventImpls.add((EventImpl) event);
		}

		Date start;
		Date end;
		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
			eventImpls = eventImpls.stream().filter(s -> s.getDate().before(end)).filter(s -> s.getDate().after(start))
					.collect(Collectors.toList());
		}

		// filter out before
		// filter out after
		// filter out events
		// if(excludeEvents != null && excludeEvents.size()>0) {

		// }

		// for now
		Module1 doughnutModule = getEventSumsByDayOfWeekAccum(eventImpls);

		ObjectMapper Obj = new ObjectMapper();

		try {

			// get Oraganisation object as a json string
			String jsonStr = Obj.writeValueAsString(doughnutModule);
			System.out.println("module1: " + jsonStr);

		} catch (Exception e) {
		}

		return doughnutModule;
	}	
	
	
	
	
	
	@Secured({ "ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT" })
	@RequestMapping(value = "/api/eventsgroupedbytimeofday/{classId}/{userId}", method = RequestMethod.GET)
	public Module1 getEventsForClassGroupedByTimeDay(@PathVariable("classId") final String classId,
			@PathVariable("userId") final String userId, @RequestParam(required = false) List<String> excludeEvents,
			@RequestParam(name = "start", required = false) String startDate,
			@RequestParam(name = "end", required = false) String endDate) throws Exception {
		log.debug("classId: {}", classId);
		log.debug("userId: {}", userId);

		// check the email to make sure that it's valid
		OpenDashboardAuthenticationToken authentication = (OpenDashboardAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		String scrubbedUserId = PulseUtility.cleanFromPulse(userId);
		String scrubbedClassId = PulseUtility.cleanFromPulse(classId);
		String tenantId = authentication.getTenantId();

		EventProvider eventProvider = providerService.getEventProvider(mongoTenantRepository.findOne(tenantId));
		Page<Event> pagedEvents = eventProvider.getEventsForCourseAndUser(tenantId, scrubbedClassId, scrubbedUserId,
				new PageRequest(0, 1000));

		// convert Events to EventImpls
		List<EventImpl> eventImpls = new ArrayList<>();
		for (Event event : pagedEvents.getContent()) {
			eventImpls.add((EventImpl) event);
		}

		Date start;
		Date end;
		if (startDate != null && endDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
			eventImpls = eventImpls.stream().filter(s -> s.getDate().before(end)).filter(s -> s.getDate().after(start))
					.collect(Collectors.toList());
		}

		// filter out before
		// filter out after
		// filter out events
		// if(excludeEvents != null && excludeEvents.size()>0) {

		// }

		// for now
		Module1 module1 = getDataByTimeOfDay(eventImpls);		
		return module1;
	}	
	
	
	
	private Module1 getEventsByCount(List<EventImpl> events) {
		  Module1 module1 = new Module1();
		  
		  
		  /*
		  // Get a list of distinct DAYS 
		  TreeSet<String> dates = new TreeSet<String>();
		  for (EventImpl event : events) { 
			  String timeStamp = event.getTimestamp();
			  String date = timeStamp.substring(11, 13);
			  dates.add(date); 
		  }
		  */
		  
		  // Get a list of hte distinct verbs 
		  
		   Map<String, Long> eventMapByCount = events
				   .stream()
				   .collect(Collectors.groupingBy(s -> s.getVerb(),
						   Collectors.counting()));
				   
		   
		  Set<String> eventsLabels = eventMapByCount.keySet();
		  // This is where the primary functionality is implemented
		  List<Module1.Dataset> datasets = new ArrayList<>();
		  List<Long> counts = new ArrayList<>();
		  int i = 0;
		  Module1.Dataset d = module1.new Dataset();
		  for (String label : eventsLabels) {	
			  Long t = eventMapByCount.get(label);
			  if(t==null) {
				counts.add(0l);  
			  }
			  else {
				  counts.add(t);
			  }
		  }
		  
		  //eventsLabels.replaceAll(s -> s.replaceFirst("day$", ""));
		  ArrayList<String> labels = new ArrayList<>();
		  for(String t: eventsLabels) {
			  labels.add(t.substring(t.indexOf("#")+1));
		  }
		  
		  d.setData(counts);
		  d.setBackgroundColor(colors.get(0));
		  datasets.add(d);
		  module1.setDatasets(datasets);
		  module1.setLabels(labels); 
		  return module1;
	}
	
	
	
	private Module1 getDataByTimeOfDay(List<EventImpl> events) {
		  Module1 module1 = new Module1();
		  
		  
		  /*
		  // Get a list of distinct DAYS 
		  TreeSet<String> dates = new TreeSet<String>();
		  for (EventImpl event : events) { 
			  String timeStamp = event.getTimestamp();
			  String date = timeStamp.substring(11, 13);
			  dates.add(date); 
		  }
		  */
		  
		  // Get a list of hte distinct verbs 
		  
		   Map<String, Long> eventMapByTimeOfDay = events
				   .stream()
				   .collect(Collectors.groupingBy(s -> s.getTimestamp().substring(11, 13),
						   Collectors.counting()));
				   
		  // This is where the primary functionality is implemented
		  List<Module1.Dataset> datasets = new ArrayList<>();
		  List<Long> counts = new ArrayList<>();
		  int i = 0;
		  Module1.Dataset d = module1.new Dataset();
		  for (String hour : hoursOfDay) {	
			  Long t = eventMapByTimeOfDay.get(hour);
			  if(t==null) {
				counts.add(0l);  
			  }
			  else {
				  counts.add(t);
			  }
		  }
		  d.setData(counts);
		  d.setBackgroundColor(colors.get(0));
		  datasets.add(d);
		  module1.setDatasets(datasets);
		  module1.setLabels(new ArrayList<String>(hoursOfDay)); 
		  return module1;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	// Create Module1
	private Module1 getEventsByDateDay(List<EventImpl> events) {
		Module1 module1 = new Module1();

		//Get max and min dates to populate the entire X Axis
		//even if the user doesn't have events for that date.
		Date maxDate = events.stream().map(EventImpl::getDate).max(Date::compareTo).get();
		Date minDate = events.stream().map(EventImpl::getDate).min(Date::compareTo).get();
		
		Calendar cStart = Calendar.getInstance(); 
		cStart.setTime(minDate);
		Calendar cEnd = Calendar.getInstance(); 
		cEnd.setTime(maxDate);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");				
		TreeSet<String> dates = new TreeSet<String>();
		while (cStart.before(cEnd)) {			
			String date = sdf.format(cStart.getTime());
			dates.add(date);
			
		    //add one day to date
		    cStart.add(Calendar.DAY_OF_MONTH, 1);		    
		}

		// Get a list of hte distinct verbs
		Set<String> distinctVerbs = events.stream().map(x -> x.getShortVerb()).collect(Collectors.toSet());

		Map<String, Map<String, Long>> eventMapByVerbAndDate = events.stream()
				.collect(Collectors.groupingBy(s -> s.getShortVerb(), // EventImpl::getVerb,
						Collectors.groupingBy(s -> s.getTimestamp().substring(0, 10), Collectors.counting())));

		// This is where the primary functionality is implemented
		List<Module1.Dataset> datasets = new ArrayList<>();
		int i = 0;
		for (String verb : distinctVerbs) {

			Module1.Dataset d = module1.new Dataset();
			d.setLabel(verb);
			Map<String, Long> verbDateCount = eventMapByVerbAndDate.get(verb);

			List<Long> counts = new ArrayList<>();
			for (String date : dates) {
				Long count = 0L;
				if (verbDateCount != null && verbDateCount.get(date) != null) {
					count = verbDateCount.get(date);
				}
				counts.add(count);
			}

			d.setBackgroundColor(colors.get(i));
			d.setData(counts);
			datasets.add(d);
			i++;
		}
		module1.setDatasets(datasets);
		module1.setLabels(new ArrayList<String>(dates));
		return module1;
	}

	ArrayList<String> colors = new ArrayList<>(Arrays.asList("#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99",
			"#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99", "#b15928"));
	
	ArrayList<String> hoursOfDay = new ArrayList<>(Arrays.asList(
			"00", "01", "02", "03", "04","05", "06", "07", "08", "09",
			"10", "11", "12", "13", "14","15", "16", "17", "18", "19",
			"20", "21", "22", "23"));
	
	ArrayList<String> dates = new ArrayList<>(
			Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
	
	
	// Create Module1 private
	DoughnutModule getEventSumsByDayOfWeekAccumDoughnut(List<EventImpl> events) {

		
		DoughnutModule module1 = new DoughnutModule();
		Map<String, Long> eventMapByDate = events.stream()
				.collect(Collectors.groupingBy(s -> s.getDayOfWeek(), Collectors.counting()));

		// This is where the primary functionality is implemented
		List<DoughnutModule.Dataset> datasets = new ArrayList<>();

		List<Long> counts = new ArrayList<>();
		DoughnutModule.Dataset d = module1.new Dataset();
		for (String date : dates) { // d.setLabel(date);
			Long count = 0L;
			if (eventMapByDate != null && eventMapByDate.get(date) != null) {
				count = eventMapByDate.get(date);
			}
			counts.add(count);
		}

		d.setData(counts);
		d.setBackgroundColor(colors);
		datasets.add(d);

		module1.setDatasets(datasets);
		module1.setLabels(new ArrayList<String>(dates));
		return module1;
	}
	
	// Create Module1 private
	Module1 getEventSumsByDayOfWeekAccum(List<EventImpl> events) {
		Module1 module1 = new Module1();

		Map<String, Long> eventMapByDate = events.stream()
				.collect(Collectors.groupingBy(s -> s.getDayOfWeek(), Collectors.counting()));

		// This is where the primary functionality is implemented
		List<Module1.Dataset> datasets = new ArrayList<>();

		List<Long> counts = new ArrayList<>();
		Module1.Dataset d = module1.new Dataset();
		for (String date : dates) { // d.setLabel(date);
			Long count = 0L;
			if (eventMapByDate != null && eventMapByDate.get(date) != null) {
				count = eventMapByDate.get(date);
			}
			counts.add(count);
		}

		d.setData(counts);
		d.setBackgroundColor(colors.get(0));
		datasets.add(d);

		module1.setDatasets(datasets);
		module1.setLabels(new ArrayList<String>(dates));
		return module1;
	}

	
	public class DoughnutModule {
		public class Dataset {
			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

			public List<Long> getData() {
				return data;
			}

			public void setData(List<Long> data) {
				this.data = data;
			}

			public List<String> getBackgroundColor() {
				return backgroundColor;
			}

			public void setBackgroundColor(List<String> backgroundColor) {
				this.backgroundColor = backgroundColor;
			}

			String label;
			List<String> backgroundColor = new ArrayList<>();
			List<Long> data = new ArrayList<>();
			// String backgroundColor;
		}

		List<String> labels = new ArrayList<>();
		List<Dataset> datasets = new ArrayList<>();

		public List<String> getLabels() {
			return labels;
		}

		public void setLabels(List<String> labels) {
			this.labels = labels;
		}

		public List<Dataset> getDatasets() {
			return datasets;
		}

		public void setDatasets(List<Dataset> datasets) {
			this.datasets = datasets;
		}
	}

	public class Module1 {
		public class Dataset {
			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

			public List<Long> getData() {
				return data;
			}

			public void setData(List<Long> data) {
				this.data = data;
			}

			public String getBackgroundColor() {
				return backgroundColor;
			}

			public void setBackgroundColor(String backgroundColor) {
				this.backgroundColor = backgroundColor;
			}

			String label;
			List<Long> data = new ArrayList<>();
			String backgroundColor;
		}

		List<String> labels = new ArrayList<>();
		List<Dataset> datasets = new ArrayList<>();

		public List<String> getLabels() {
			return labels;
		}

		public void setLabels(List<String> labels) {
			this.labels = labels;
		}

		public List<Dataset> getDatasets() {
			return datasets;
		}

		public void setDatasets(List<Dataset> datasets) {
			this.datasets = datasets;
		}
	}

}
