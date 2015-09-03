/**
 * 
 */
package od.providers.course.sakai;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.course.CourseProvider;
import od.providers.sakai.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("courses_sakai")
public class SakaiCourseProvider extends BaseSakaiProvider implements CourseProvider {
  
  private final String COLLECTION_URI = "/direct/site.json";
  private final String ENTITY_URI ="/direct/site/{ID}.json";
  
  private static final String KEY = "courses_sakai";
  private static final String NAME = "Sakai Courses Web Service";
  private ProviderConfiguration providerConfiguration;
  
  @PostConstruct
  public void init() {
    
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    
    providerConfiguration = new DefaultProviderConfiguration(options);
  }


  @Override
  public Course getContext(ProviderOptions options) throws ProviderException {
    String url = fullUrl(options.getStrategyHost(), StringUtils.replace(ENTITY_URI, "{ID}", options.getCourseId()));
    ResponseEntity<CourseImpl> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), CourseImpl.class);
    return messageResponse.getBody();
  }

  @Override
  public List<CourseImpl> getContexts(ProviderOptions options) throws ProviderException {
    String url = fullUrl(options.getStrategyHost(), COLLECTION_URI);
    ResponseEntity<SakaiSiteCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiSiteCollection.class);
    return messageResponse.getBody().getSite_collection();
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
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }
  
}


