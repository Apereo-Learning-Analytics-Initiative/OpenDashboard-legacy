/**
 * 
 */
package od.providers.course.learninglocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.api.PageWrapper;
import od.providers.course.CourseProvider;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.learninglocker.LearningLockerStaffModuleInstance;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
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
  
  private boolean OAUTH = false;
  private boolean DEMO = true;
  
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

  private PageImpl<LearningLockerModuleInstance> fetch(Pageable pageable, Tenant tenant, String path) {
    
    log.debug("{}",path);
    
    ProviderData providerData = tenant.findByKey(KEY);

    String url = providerData.findValueForKey("base_url");
    if (!url.endsWith("/") && !path.startsWith("/")) {
      url = url + "/";
    }
    
    url = url + path;
    
    ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
    resourceDetails.setClientId(providerData.findValueForKey("key"));
    resourceDetails.setClientSecret(providerData.findValueForKey("secret"));
    resourceDetails.setAccessTokenUri(getUrl(providerData.findValueForKey("base_url"), LL_OAUTH_TOKEN_URI, null));
    DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
    ParameterizedTypeReference<PageWrapper<LearningLockerModuleInstance>> responseType = new ParameterizedTypeReference<PageWrapper<LearningLockerModuleInstance>>() {};
    
    PageWrapper<LearningLockerModuleInstance> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, null, responseType).getBody();
    log.debug(pageWrapper.toString());
    List<LearningLockerModuleInstance> output;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      output = new LinkedList<>(pageWrapper.getContent());
    }
    else {
      output = new ArrayList<>();
    }
    
    return new PageImpl<LearningLockerModuleInstance>(output, pageable, pageWrapper.getPage().getTotalElements());
  }


  @Override
  public Course getContext(ProviderOptions options) throws ProviderException {
    CourseImpl course = null;
    
    String path = "/api/v1/LearningLockerModuleInstances?query={\"_id\":\"%s\"}&populate=MODULE";
    path = String.format(path, options.getCourseId());
    
    Pageable pageable = new PageRequest(0, 1);  
    Tenant tenant = mongoTenantRepository.findOne(options.getTenantId());
    PageImpl<LearningLockerModuleInstance> page = fetch(pageable, tenant, path);
    
    if (page != null && page.hasContent()) {
      List<LearningLockerModuleInstance> LearningLockerModuleInstances = page.getContent();
      if (LearningLockerModuleInstances != null && !LearningLockerModuleInstances.isEmpty()) {
        LearningLockerModuleInstance learningLockerModuleInstance = LearningLockerModuleInstances.get(0);
        course = new CourseImpl();
        course.setId(options.getCourseId());
        course.setTitle(learningLockerModuleInstance.getModule().getModName());
      }
    }
    
    return course;
  }

  @Override
  public List<Course> getContexts(ProviderOptions options) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Course> getContextsForUser(ProviderOptions options) throws ProviderException {
    
    
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

    Tenant tenant = mongoTenantRepository.findOne(options.getTenantId());
    ProviderData providerData = tenant.findByKey(KEY);
    String baseUrl = providerData.findValueForKey("base_url");
    if (!baseUrl.endsWith("/")) {
      baseUrl = baseUrl + "/";
    }

    RestTemplate restTemplate;
    
    if (OAUTH) {
      ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
      resourceDetails.setClientId(providerData.findValueForKey("key"));
      resourceDetails.setClientSecret(providerData.findValueForKey("secret"));
      resourceDetails.setAccessTokenUri(getUrl(baseUrl, LL_OAUTH_TOKEN_URI, null));
      DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

      restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
    }
    else {
      restTemplate = new RestTemplate();
    }
    
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
    
    String staffModuleInstancePath = ""; // TODO
    LearningLockerStaffModuleInstance [] staffModuleInstances = restTemplate.exchange(baseUrl + staffModuleInstancePath, 
        HttpMethod.GET, null, LearningLockerStaffModuleInstance[].class, options.getUserId()).getBody();
      
    if (staffModuleInstances != null && staffModuleInstances.length > 0) {
      StringBuilder result = new StringBuilder();
      for(LearningLockerStaffModuleInstance smi : staffModuleInstances) {
        result.append("'");
        result.append("TBD"); // TODO
        result.append("'");
        result.append(",");
      }
      String staffModuleIdList = result.length() > 0 ? result.substring(0, result.length() - 1): "";
      
      String moduleInstancePath = ""; // TODO
      String queryValue = String.format("", staffModuleIdList); // TODO
      LearningLockerModuleInstance [] moduleInstances = restTemplate.exchange(baseUrl + moduleInstancePath, HttpMethod.GET, null, LearningLockerModuleInstance[].class, queryValue).getBody();
      
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
  public String getLTIContextIdByCourseId(String courseId) throws ProviderException {
    // TODO Auto-generated method stub
    return "13";
  }

  private Course toCourse(LearningLockerModuleInstance llModuleInstance) {
    CourseImpl course = new CourseImpl();
    course.setId(llModuleInstance.getModInstanceId());
    course.setTitle(llModuleInstance.getModule().getModName());
    return course;
  }

  @Override
  public String getCourseIdByLTIContextId(String ltiContextId) throws ProviderException {
    // TODO Auto-generated method stub
    return "1";
  }

}
