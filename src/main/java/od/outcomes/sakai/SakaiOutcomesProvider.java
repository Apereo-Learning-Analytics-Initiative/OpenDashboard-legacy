/**
 * 
 */
package od.outcomes.sakai;

import java.util.List;

import od.outcomes.LineItem;
import od.outcomes.OutcomesProvider;
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
public class SakaiOutcomesProvider extends BaseSakaiProvider implements OutcomesProvider {
  
  private final String ENTITY_URI = "/direct/grades/gradebook/{ID}.json";

  @Override
  public List<LineItem> getOutcomesForCourse(ProviderOptions options) throws ProviderException {
    
    List<LineItem> lineItems = null;
    
    String host = options.getStrategyHost();
    if (StringUtils.isBlank(host)) {
      host = this.sakaiHost;
    }
    
    String url = fullUrl(host, StringUtils.replace(ENTITY_URI, "{ID}", options.getCourseId()));
    ResponseEntity<SakaiGradebook> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiGradebook.class);
    
    if (messageResponse != null) {
      SakaiGradebook sakaiGradebook = messageResponse.getBody();
      lineItems = sakaiGradebook.toLineItems();
    }
    
    return lineItems;
  }

}
