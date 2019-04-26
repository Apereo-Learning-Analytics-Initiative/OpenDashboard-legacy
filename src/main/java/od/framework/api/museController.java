package od.framework.api;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import od.framework.model.MuseCourse;
import od.framework.model.MuseCourseList;
import od.framework.model.MuseNotify;
import od.framework.model.MuseApi;
import od.framework.model.MuseStudent;
import od.framework.model.Tenant;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.enrollment.EnrollmentProvider;
import od.providers.user.UserProvider;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;

@RestController
public class museController {
  
  private static final Logger log = LoggerFactory.getLogger(museController.class);
  
  // Get YAML config to turn on and off student, instructor and default views.
  
  @Value("${opendashboard.enableStudentView}")
  private Boolean enableStudentView;

  @Value("${opendashboard.enableInstructorView}")
  private Boolean enableInstructorView;

  @Value("${opendashboard.enableDefaultView}")
  private Boolean enableDefaultView;
  
  @Value("${opendashboard.api.url}")
  private String apiLocUrl;

  @Autowired private ProviderService providerService;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR","ROLE_STUDENT"})
  @RequestMapping(value = "/api/tenants/{tenantId}/muse/{userId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public MuseCourseList museCourseList(Authentication authentication
		  , @PathVariable("tenantId") final String tenantId
		  , @PathVariable("userId") final String userId
		  ) throws ProviderDataConfigurationException, ProviderException {
	  
		  Tenant tenant = mongoTenantRepository.findOne(tenantId);
		  EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
		  ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
		  
		  // Gets all authorities (roles) associated with an LTI user.
		  Collection<? extends GrantedAuthority> myAuthentication = authentication.getAuthorities();
		  Set<Enrollment> myEnrollments = enrollmentProvider.getEnrollmentsForUser(rosterProviderData, userId, true) ;
		  // Gets all metadata for each course and student in the muse course list.

		  for (Enrollment value : myEnrollments) {
			  Map<String, String> studentMetadata = value.getMetadata();
			  Map<String, String> classMetadata = value.getKlass().getMetadata();
			  
			  // Take out the appropriate data for student view.
			  if(value.getRole().toString() == "student"){
				  studentMetadata.put("http://unicon.net/vocabulary/v1/enrollmentStatistics", "null");
			  	  classMetadata.put("http://unicon.net/vocabulary/v1/classStatistics", "null");
			  }
		  }
			  	  
		  MuseCourseList retCourseList = new MuseCourseList.Builder()
										  .withEnrollment(myEnrollments)
										  .withAuthentication(myAuthentication)
										  .withAllowInstructor(enableInstructorView)
										  .withAllowStudent(enableStudentView)
										  .withAllowDefault(enableDefaultView)
										  .build();
	  return retCourseList;
  }
  
  
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR","ROLE_STUDENT"})
  @RequestMapping(value = "/api/museNotify/{tenantId}", method = RequestMethod.POST, 
      consumes = "application/json;charset=utf-8")
  public MuseNotify museNotify(Authentication authentication
		  , @PathVariable("tenantId") final String tenantId
		  , @RequestBody MuseNotify notification
		  ) throws ProviderDataConfigurationException, ProviderException {
	  
	  String retVal = "Failure";

	  	  Tenant tenant = mongoTenantRepository.findOne(tenantId);
		  UserProvider userProvider = providerService.getUserProvider(tenant);
		  ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.USER);
		  retVal = userProvider.notifyUser(rosterProviderData, notification);
		  MuseNotify musenotify = new MuseNotify.Builder()
				  				.withstatus(retVal)
				  				.build();
	 	  return musenotify;	  
  } 
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR"})
  @RequestMapping(value = "/api/museAPI/{tenantId}/{apiUrl}", method = RequestMethod.POST, 
      consumes = "application/json;charset=utf-8")
  public MuseApi museApi(Authentication authentication
		  , @PathVariable("tenantId") final String tenantId
		  , @PathVariable("apiUrl") String apiUrl
		  , @RequestBody MuseApi dataObj
		  ) throws ProviderDataConfigurationException, ProviderException {

	  String retVal = "Failure";
	  String retData = null;

	  HttpURLConnection connection = null;
	  
	  // Change dashes to slashes
	  apiUrl = apiUrl.replaceAll("-", "/");
	 
	  try {
		  
		  // Create connection
		  URL url = new URL(apiLocUrl + "/" + apiUrl);
		  connection = (HttpURLConnection) url.openConnection();
		  connection.setRequestMethod("POST");
		  connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

	      connection.setUseCaches(false);
	      connection.setDoOutput(true);
		  
		  // Send request
		  DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		  wr.writeBytes(dataObj.getdata());
		  wr.close();
		  
		  // Get response
		  InputStream is = connection.getInputStream();
		  BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		  StringBuffer response = new StringBuffer();
		  String line;
		  while ((line = rd.readLine()) != null){
			  response.append(line);
			  response.append('\r');
		  }
		  rd.close();
		  
		  retData = response.toString(); 
		  retVal = "Success";
		  
	  }catch(Exception e) {
		  System.out.println(e);
		  log.error(e.getMessage(), e);
	  }finally {
	    if (connection != null) {
	        connection.disconnect();
	    }
	  }
	  
	  
	  MuseApi museapi = new MuseApi.Builder()
				.withstatus(retVal)
				.withdata(retData)
				.build();
	  return museapi;
  }
  
  
  
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR"})
  @RequestMapping(value = "/api/tenants/{tenantId}/muse/{userId}/course/{courseId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public MuseCourse museCourse(Authentication authentication
		  , @PathVariable("tenantId") final String tenantId
	      , @PathVariable("userId") final String userId
	      , @PathVariable("courseId") final String courseId
      ) throws ProviderDataConfigurationException, ProviderException {
	  
	  Tenant tenant = mongoTenantRepository.findOne(tenantId);
	  ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
	  
      CourseProvider courseProvider = providerService.getCourseProvider(tenant);
      Class course = courseProvider.getClass(tenant, courseId);
      
      EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
      Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForClass(rosterProviderData, courseId, true);
      
      
      Enrollment selfEnrollment = null;
      
      for (Enrollment e: enrollments){
    	  String s = e.getUser().getUserId();
    	  if(s.equalsIgnoreCase(userId)){
    		  selfEnrollment = e;
    		  break;
    	  }
      }

      if(selfEnrollment.getRole().toString() == "student"){
    	  MuseCourse retCourse = new MuseCourse.Builder()
					.withEnrollments(null)
					.withCourse(null)
					.build();
    	  return retCourse;
      }else{
	      MuseCourse retCourse = new MuseCourse.Builder()
	    		  					.withEnrollments(enrollments)
	    		  					.withCourse(course)
	    		  					.build();
	      return retCourse;
      }
  }
  
  @Secured({"ROLE_ADMIN","ROLE_INSTRUCTOR","ROLE_STUDENT"})
  @RequestMapping(value = "/api/tenants/{tenantId}/muse/{userId}/course/{courseId}/student/{studentId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public MuseStudent museCourseStudent(Authentication authentication
		  , @PathVariable("tenantId") final String tenantId
	      , @PathVariable("userId") final String userId
	      , @PathVariable("courseId") final String courseId
	      , @PathVariable("studentId") final String studentId
      ) throws ProviderDataConfigurationException, ProviderException {
	  
	  Tenant tenant = mongoTenantRepository.findOne(tenantId);
	  ProviderData rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);
      EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
      
      Enrollment myEnrollment = null;
      Enrollment selfEnrollment = null;
      Collection<? extends GrantedAuthority> myAuthentication = authentication.getAuthorities();
      
      Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForClass(rosterProviderData, courseId, true);
      for (Enrollment e: enrollments){
    	  String s = e.getUser().getUserId();
    	  if (s.equalsIgnoreCase(studentId)){
    		  myEnrollment = e;
    	  }
    	  if(s.equalsIgnoreCase(userId)){
    		  selfEnrollment = e;
    	  }
    	  if(myEnrollment != null && selfEnrollment != null){
    		  break;
    	  }
      }
	  
  		//Check if user is a student
  		if (selfEnrollment.getRole().toString() == "student") {
  			
  			// get metadata of user
  			Map<String, String> metadata = myEnrollment.getMetadata();
  			String metaString = metadata.get("http://unicon.net/vocabulary/v1/enrollmentStatistics");
  			
  		    // Replaceall using RegEx (Regular Expressions): Replaces isAtRisk and successProbability.
  			metaString = metaString.replaceAll("\"isAtRisk\": \"(.*?)\", ", "")
  					               .replaceAll(", \"successProbability\": \\d.\\d\\d", "")
  					               .replaceAll(", \"weeklyProbability\": " + "\\" + "[(.*?)]", "");
  			
  		    // Put the modified string back into metadata referencing the key.
			metadata.put("http://unicon.net/vocabulary/v1/enrollmentStatistics", metaString);
			
			
			// get metadata of class
			Map<String, String> meta = myEnrollment.getKlass().getMetadata();
			String met = meta.get("http://unicon.net/vocabulary/v1/classStatistics");
			
			// Replaceall using RegEx (Regular Expressions): Replaces atRiskCount, enrollment, Alert Notification, and museNotifySubject.
			met = metaString.replaceAll("\"atRiskCount\": (.*?), ", "")
					        .replaceAll(", \"enrollment\": (.*?), \"museNotifyMessage\": \"(.*?)\", \"museNotifySubject\": \"(.*?)\"", "");
			
			// Put the modified string back into metadata referencing the key.
			meta.put("http://unicon.net/vocabulary/v1/classStatistics", met);
  		}
	   

      MuseStudent retStudent = new MuseStudent.Builder()
    		  					.withEnrollment(myEnrollment)
    		  					.withAuthentication(myAuthentication)
    		  					.build();
      
	  return retStudent;
  }
  
}
