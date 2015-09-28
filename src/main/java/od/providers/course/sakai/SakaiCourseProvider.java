/**
 * 
 */
package od.providers.course.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.ProviderConfiguration;
import od.providers.course.CourseProvider;
import od.providers.sakai.BaseSakaiProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final String BASE = "SAKAI_COURSES_WEB_SERVICE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;

  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultSakaiProviderConfiguration();
  }

  @Override
  public Course getContext(ProviderOptions options) throws ProviderException {
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    String url = fullUrl(providerData, StringUtils.replace(ENTITY_URI, "{ID}", options.getCourseId()));
    ResponseEntity<CourseImpl> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), CourseImpl.class);
    return messageResponse.getBody();
  }

  @Override
  public List<CourseImpl> getContexts(ProviderOptions options) throws ProviderException {
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    String url = fullUrl(providerData, COLLECTION_URI);
    ResponseEntity<SakaiSiteCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiSiteCollection.class);
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
  public String getDesc() {
    return DESC;
}

@Override
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }
  
}


