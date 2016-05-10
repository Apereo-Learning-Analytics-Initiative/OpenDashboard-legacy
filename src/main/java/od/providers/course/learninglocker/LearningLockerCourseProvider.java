/**
 * 
 */
package od.providers.course.learninglocker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.course.CourseProvider;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.learninglocker.LearningLockerStaffModuleInstance;
import od.repository.mongo.MongoTenantRepository;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    String staffUrl = buildUrl(baseUrl, STAFF_URI);
    MultiValueMap<String, String> staffParams = new LinkedMultiValueMap<String, String>();
    staffParams.add("query", String.format("{\"STAFF_ID\":\"%s\"}", userId));
    
    log.debug(buildUri(staffUrl, staffParams).toString());
    
    ResponseEntity<LearningLockerStaff[]> responseEntity 
      = restTemplate.exchange(buildUri(staffUrl,staffParams), HttpMethod.GET, 
          headers, LearningLockerStaff[].class);
    
    if (responseEntity == null) {
      log.error(String.format("ResponseEntity null for {} {}", staffUrl, staffParams));
      throw new ProviderException(ProviderException.NO_STAFF_ENTRY_ERROR_CODE);
    }
    
    LearningLockerStaff [] staff = responseEntity.getBody();

    String staffId = null;
    if (staff != null && staff.length > 0) {
      if (staff.length == 1) {
        LearningLockerStaff s = staff[0];
        staffId = s.getStaffId();
      }
      else {
        log.error(String.format("Too many staff entries for user id %s", userId));
        throw new ProviderException(ProviderException.TOO_MANY_STAFF_ENTRIES_ERROR_CODE);
      }
    }
    else {
      log.error(String.format("No staff entries for user id %s", userId));
      throw new ProviderException(ProviderException.NO_STAFF_ENTRY_ERROR_CODE);
    }
    
    log.debug("staff id is {}", staffId);
    
    String staffModuleInstanceUrl = buildUrl(baseUrl, STAFF_MODULE_INSTANCE_URI);
    MultiValueMap<String, String> staffModuleInstanceParams = new LinkedMultiValueMap<String, String>();
    staffModuleInstanceParams.add("query", String.format("{\"STAFF_ID\":\"%s\"}", staffId));
    
    log.debug(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams).toString());
    
    LearningLockerStaffModuleInstance [] staffModuleInstances 
      = restTemplate.exchange(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams), 
        HttpMethod.GET, headers, LearningLockerStaffModuleInstance[].class).getBody();
      
    if (staffModuleInstances != null && staffModuleInstances.length > 0) {
      StringBuilder result = new StringBuilder();
      for(LearningLockerStaffModuleInstance smi : staffModuleInstances) {
        result.append("\"");
        result.append(smi.getModInstanceId());
        result.append("\"");
        result.append(",");
      }
      
      String staffModuleIdList = result.length() > 0 ? result.substring(0, result.length() - 1): null;
      
      if (StringUtils.isBlank(staffModuleIdList)) {
        throw new ProviderException(String.format("No staff module instances for %s %s",staffModuleInstanceUrl, staffModuleInstanceParams));
      }
      
      String moduleInstanceUrl = buildUrl(baseUrl, MODULE_INSTANCE_URI);
      MultiValueMap<String, String> moduleInstanceParams = new LinkedMultiValueMap<String, String>();
      moduleInstanceParams.add("query", String.format("{\"MOD_INSTANCE_ID\":[%s]}", staffModuleIdList));
      
      log.debug(buildUri(moduleInstanceUrl, moduleInstanceParams).toString());

      LearningLockerModuleInstance [] moduleInstances = restTemplate.exchange(buildUri(moduleInstanceUrl, moduleInstanceParams), HttpMethod.GET, headers, LearningLockerModuleInstance[].class).getBody();
      
      if (moduleInstances != null && moduleInstances.length > 0) {
        output = new ArrayList<>();
        
        for (LearningLockerModuleInstance mi : moduleInstances) {
          output.add(toCourse(mi));
        }
      }
      else {
        output = new ArrayList<>();
      }
    }
    
    return output;
  }
  
  @Override
  public String getCourseIdByLTIContextId(Tenant tenant, String ltiContextId) throws ProviderException {
    
    if (DEMO) {
      return "13";
    }

    String courseId = null;
    
    ProviderData providerData = tenant.findByKey(KEY);
    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
    String moduleVleMapUrl = buildUrl(baseUrl, MODULE_VLE_MAP_URI);
    MultiValueMap<String, String> moduleVleMapParams = new LinkedMultiValueMap<String, String>();
    moduleVleMapParams.add("query", String.format("{\"VLE_MOD_ID\":\"%s\"}", ltiContextId));
    
    log.debug(buildUri(moduleVleMapUrl, moduleVleMapParams).toString());

    LearningLockerVLEModuleMap [] moduleVleMaps = restTemplate.exchange(buildUri(moduleVleMapUrl, moduleVleMapParams), HttpMethod.GET, headers, LearningLockerVLEModuleMap[].class).getBody();
    
    if (moduleVleMaps != null && moduleVleMaps.length > 0) {
      if (moduleVleMaps.length == 1) {
        LearningLockerVLEModuleMap moduleMap = moduleVleMaps[0];
        courseId = moduleMap.getModuleInstanceId();
      }
      else {
        log.error(String.format("Too many vle module maps for tenant %s lti context %s", tenant.getId(), ltiContextId));
        throw new ProviderException(ProviderException.TOO_MANY_VLE_MODULE_MAPS_ERROR_CODE);
      }
    }
    else {
      log.error(String.format("No vle module maps for tenant %s lti context %s", tenant.getId(), ltiContextId));
      throw new ProviderException(ProviderException.NO_VLE_MODULE_MAPS_ERROR_CODE);
    }
    
    log.debug("course id is {} for tenant {} lti context {}", courseId, tenant.getId(), ltiContextId);
    return courseId;
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
  public LearningLockerStaff getStaffWithPid(Tenant tenant, String pid) throws ProviderException {
    
    ProviderData providerData = tenant.findByKey(KEY);

    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
    String staffUrl = buildUrl(baseUrl, STAFF_URI);
    MultiValueMap<String, String> staffParams = new LinkedMultiValueMap<String, String>();
    try {
      staffParams.add("query", String.format("{\"DASH_SHIB_ID\":\"%s\"}", URLEncoder.encode(pid, "UTF-8")));
    } 
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    
    log.debug(buildUri(staffUrl, staffParams).toString());
    
    ResponseEntity<LearningLockerStaff[]> responseEntity 
      = restTemplate.exchange(buildUri(staffUrl,staffParams), HttpMethod.GET, 
          headers, LearningLockerStaff[].class);
    
    if (responseEntity == null || responseEntity.getBody() == null || responseEntity.getBody().length == 0) {
      log.error(String.format("ResponseEntity null for %s %s", staffUrl, staffParams));
      throw new ProviderException(ProviderException.NO_STAFF_ENTRY_ERROR_CODE);
    }
    
    return responseEntity.getBody()[0];
  }

}
