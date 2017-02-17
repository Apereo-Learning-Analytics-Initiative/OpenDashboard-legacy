/**
 * 
 */
package od.providers.course.learninglocker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.course.CourseProvider;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.learninglocker.LearningLockerStaffModuleInstance;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.oneroster.Class;

/**
 * @author ggilbert
 *
 */
@Component("courses_learninglocker")
public class LearningLockerCourseProvider extends LearningLockerProvider implements CourseProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LearningLockerCourseProvider.class);
  
  private static final String KEY = "courses_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
      
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultLearningLockerConfiguration();
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public Course getContext(ProviderData providerData, String contextId) throws ProviderException {
    Course course = null;
    
    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");

    String moduleInstanceUrl = buildUrl(baseUrl, MODULE_INSTANCE_URI);
    MultiValueMap<String, String> moduleInstanceParams = new LinkedMultiValueMap<String, String>();
    moduleInstanceParams.add("query", String.format("{\"MOD_INSTANCE_ID\":\"%s\"}", contextId));
    
    log.debug(buildUri(moduleInstanceUrl, moduleInstanceParams).toString());

    LearningLockerModuleInstance [] moduleInstances = restTemplate.exchange(buildUri(moduleInstanceUrl, moduleInstanceParams), HttpMethod.GET, headers, LearningLockerModuleInstance[].class).getBody();
    
    if (moduleInstances != null && moduleInstances.length > 0) {
      if (moduleInstances.length == 1) {
        course = toCourse(moduleInstances[0]);
      }
      else {
        log.warn(String.format("Found multiple module instances for contextId: {}", contextId));
        log.warn("Using last in list");
        for (LearningLockerModuleInstance mi : moduleInstances) {
          course = toCourse(mi);
        }
      }
    }
    else {
      log.error(String.format("No module instances found for contextId: {}", contextId));
      throw new ProviderException(ProviderException.NO_MODULE_INSTANCES_ERROR_CODE);
    }
    
    return course;
  }
  
  @Override
  public List<Course> getContexts(ProviderData providerData, String userId) throws ProviderException {
    
    
    if (DEMO) {
      CourseImpl course1 = new CourseImpl();
      course1.setId("1");
      course1.setTitle("Course 1");
      
      CourseImpl course2 = new CourseImpl();
      course2.setId("2");
      course2.setTitle("Course 2");

      List<Course> courses = new ArrayList<Course>();
      courses.add(course1);
      courses.add(course2);
      
      return courses;
    }
    
    List<Course> output = null;

    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
    
    log.debug("staff id is {}", userId);
    
    String staffModuleInstanceUrl = buildUrl(baseUrl, STAFF_MODULE_INSTANCE_URI);
    MultiValueMap<String, String> staffModuleInstanceParams = new LinkedMultiValueMap<String, String>();
    staffModuleInstanceParams.add("query", String.format("{\"STAFF_ID\":\"%s\"}", userId));
    staffModuleInstanceParams.add("populate", "{\"path\":\"moduleInstance\",\"populate\":{\"path\":\"module\"}}");
    
    log.debug(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams).toString());
    
    LearningLockerStaffModuleInstance [] staffModuleInstances 
      = restTemplate.exchange(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams), 
        HttpMethod.GET, headers, LearningLockerStaffModuleInstance[].class).getBody();
      
    if (staffModuleInstances != null && staffModuleInstances.length > 0) {
      output = new ArrayList<>();
      for(LearningLockerStaffModuleInstance smi : staffModuleInstances) {
        output.add(toCourse(smi.getModuleInstance()));
      }
      
    }
    
    return output;
  }
  
  @Override
  public String getClassSourcedIdWithExternalId(Tenant tenant, String ltiContextId) throws ProviderException {
    
    List<String> courseIds = null;
    
    ProviderData providerData = tenant.findByKey(KEY);
    
    if (providerData == null) {
      throw new ProviderException(ProviderException.MISSING_PROVIDER_DATA);
    }
    
    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
    String moduleVleMapUrl = buildUrl(baseUrl, MODULE_VLE_MAP_URI);
    MultiValueMap<String, String> moduleVleMapParams = new LinkedMultiValueMap<String, String>();
    moduleVleMapParams.add("query", String.format("{\"VLE_MOD_ID\":\"%s\"}", ltiContextId));
    
    log.debug(buildUri(moduleVleMapUrl, moduleVleMapParams).toString());

    LearningLockerVLEModuleMap [] moduleVleMaps = restTemplate.exchange(buildUri(moduleVleMapUrl, moduleVleMapParams), HttpMethod.GET, headers, LearningLockerVLEModuleMap[].class).getBody();
    
    if (moduleVleMaps != null && moduleVleMaps.length > 0) {
      courseIds = new ArrayList<>();
      for (LearningLockerVLEModuleMap maps : moduleVleMaps) {
        courseIds.add(maps.getModuleInstanceId());
      }
//      if (moduleVleMaps.length == 1) {
//        LearningLockerVLEModuleMap moduleMap = moduleVleMaps[0];
//        courseId = moduleMap.getModuleInstanceId();
//      }
//      else {
//        log.error(String.format("Too many vle module maps for tenant %s lti context %s", tenant.getId(), ltiContextId));
//        throw new ProviderException(ProviderException.TOO_MANY_VLE_MODULE_MAPS_ERROR_CODE);
//      }
    }
    else {
      log.error(String.format("No vle module maps for tenant %s lti context %s", tenant.getId(), ltiContextId));
      throw new ProviderException(ProviderException.NO_VLE_MODULE_MAPS_ERROR_CODE);
    }
    
    log.debug("course id is {} for tenant {} lti context {}", courseIds, tenant.getId(), ltiContextId);
    return courseIds.get(0);
  }
  
  private Course toCourse(LearningLockerModuleInstance llModuleInstance) {
    CourseImpl course = new CourseImpl();
    course.setId(llModuleInstance.getModInstanceId());
    if (llModuleInstance.getModule() != null) {
      course.setTitle(llModuleInstance.getModule().getModName());
    }
    else {
      course.setTitle(llModuleInstance.getModInstanceId());
    }
    return course;
  }

  @Override
  public Class getClass(Tenant tenant, String classSourcedId) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

//  @Override
//  public String getStaffIdWithPid(Tenant tenant, String pid) throws ProviderException {
//    
//    ProviderData providerData = tenant.findByKey(KEY);
//    
//    if (providerData == null) {
//      throw new ProviderException(ProviderException.MISSING_PROVIDER_DATA);
//    }
//
//    RestTemplate restTemplate = getRestTemplate(providerData);
//    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
//    String baseUrl = providerData.findValueForKey("base_url");
//    String staffUrl = buildUrl(baseUrl, STAFF_URI);
//    MultiValueMap<String, String> staffParams = new LinkedMultiValueMap<String, String>();
//    staffParams.add("query", String.format("{\"DASH_SHIB_ID\":{\"$regex\":\"'%s'\"}}", pid));
//    
//    log.debug(buildUri(staffUrl, staffParams).toString());
//    
//    ResponseEntity<LearningLockerStaff[]> responseEntity 
//      = restTemplate.exchange(buildUri(staffUrl,staffParams), HttpMethod.GET, 
//          headers, LearningLockerStaff[].class);
//    
//    if (responseEntity == null || responseEntity.getBody() == null || responseEntity.getBody().length == 0) {
//      log.error(String.format("ResponseEntity null for %s %s", staffUrl, staffParams));
//      throw new ProviderException(ProviderException.NO_STAFF_ENTRY_ERROR_CODE);
//    }
//    
//    LearningLockerStaff staff = responseEntity.getBody()[0];
//    String staffId = null;
//    if (staff != null) {
//      staffId = staff.getStaffId();
//    }
//    
//    return staffId;
//  }

}
