/**
 * 
 */
package od.providers.outcomes.sakai;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.outcomes.OutcomesProvider;
import od.providers.sakai.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.impl.LineItemImpl;
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
  private static final String NAME = "Sakai Outcomes Web Service";
  private ProviderConfiguration providerConfiguration;
  
  @PostConstruct
  public void init() {
    
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    
    providerConfiguration = new DefaultProviderConfiguration(options);
  }


  @Override
  public List<LineItemImpl> getOutcomesForCourse(ProviderOptions options) throws ProviderException {
    
    List<LineItemImpl> lineItems = null;
    
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
