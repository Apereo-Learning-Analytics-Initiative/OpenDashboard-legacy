/**
 * 
 */
package od.providers.course;

import java.util.List;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.events.MatthewsEventProvider;
import od.providers.matthews.MatthewsClient;
import od.providers.matthews.MatthewsProvider;
import od.repository.mongo.MongoTenantRepository;

import org.apereo.lai.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.entity.ClassMapping;
import unicon.matthews.entity.UserMapping;
import unicon.matthews.oneroster.Class;

/**
 * @author ggilbert
 *
 */
@Component("course_matthews")
public class MatthewsCourseProvider extends MatthewsProvider implements CourseProvider {

  private static final Logger log = LoggerFactory.getLogger(MatthewsEventProvider.class);
  
  private static final String KEY = "course_matthews";
  private static final String BASE = "MATTHEWS_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  @Autowired private MongoTenantRepository mongoTenantRepository;

  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultMatthewsConfiguration();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Course> getContexts(ProviderData providerData, String userId) throws ProviderException {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see od.providers.course.CourseProvider#getClassSourcedIdWithExternalId(od.framework.model.Tenant, java.lang.String)
   */
  @Override
  public String getClassSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
    ProviderData providerData = tenant.findByKey(KEY);

    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/mapping/").concat(externalId);
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<ClassMapping> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), ClassMapping.class);
    
    if (response.getStatusCode() == HttpStatus.NOT_FOUND 
        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR 
        || response.getBody() == null) {
      return null;
    }
    
    ClassMapping classMapping = response.getBody();
    
    return classMapping.getClassSourcedId();
  }

  @Override
  public Class getClass(Tenant tenant, String classSourcedId) throws ProviderException {
    ProviderData providerData = tenant.findByKey(KEY);

    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/").concat(classSourcedId);
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<Class> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), Class.class);
    
    return response.getBody();
  }

}
