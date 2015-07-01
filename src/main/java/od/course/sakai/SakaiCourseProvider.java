/**
 * 
 */
package od.course.sakai;

import java.util.List;

import od.course.Course;
import od.course.CourseProvider;
import od.providers.BaseSakaiProvider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class SakaiCourseProvider extends BaseSakaiProvider implements CourseProvider {
  
  private final String COLLECTION_URI = "/direct/site.json";
  private final String ENTITY_URI ="/direct/site/{ID}.json";

  @Override
  public Course getContext(ProviderOptions options) throws ProviderException {
    String url = fullUrl(options.getStrategyHost(), StringUtils.replace(ENTITY_URI, "{ID}", options.getCourseId()));
    ResponseEntity<Course> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), Course.class);
    return messageResponse.getBody();
  }

  @Override
  public List<Course> getContexts(ProviderOptions options) throws ProviderException {
    String url = fullUrl(options.getStrategyHost(), COLLECTION_URI);
    ResponseEntity<SakaiSiteCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiSiteCollection.class);
    return messageResponse.getBody().getSite_collection();
  }
  
}


