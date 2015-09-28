/**
 * 
 */
package od.providers.outcomes.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.ProviderConfiguration;
import od.providers.outcomes.OutcomesProvider;
import od.providers.sakai.BaseSakaiProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.impl.LineItemImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("outcomes_sakai")
public class SakaiOutcomesProvider extends BaseSakaiProvider implements OutcomesProvider {
  
  private final String ENTITY_URI = "/direct/grades/gradebook/{ID}.json";
  
  private static final String KEY = "outcomes_sakai";
  private static final String BASE = "SAKAI_OUTCOMES_WEB_SERVICE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;

  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultSakaiProviderConfiguration();
  }

  @Override
  public List<LineItemImpl> getOutcomesForCourse(ProviderOptions options) throws ProviderException {
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    List<LineItemImpl> lineItems = null;
    
    String url = fullUrl(providerData, StringUtils.replace(ENTITY_URI, "{ID}", options.getCourseId()));
    ResponseEntity<SakaiGradebook> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiGradebook.class);
    
    if (messageResponse != null) {
      SakaiGradebook sakaiGradebook = messageResponse.getBody();
      lineItems = sakaiGradebook.toLineItems();
    }
    
    return lineItems;
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
