/**
 * 
 */
package od.providers.assignment.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderOptions;
import od.providers.assignment.AssignmentsProvider;
import od.providers.config.ProviderConfiguration;
import od.providers.sakai.BaseSakaiProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.impl.AssignmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author mflynn
 *
 */
@Component("assignments_sakai")
public class SakaiAssignmentsProvider extends BaseSakaiProvider implements AssignmentsProvider {

  private static final Logger log = LoggerFactory.getLogger(SakaiAssignmentsProvider.class);
  private final String COLLECTION_URI = "/direct/assignment/site/{ID}.json";
  
  private static final String KEY = "assignments_sakai";
  private static final String NAME = "Sakai Assignments Web Service";
  private ProviderConfiguration providerConfiguration;
  
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultSakaiProviderConfiguration();
  }

  @Override
  public List<AssignmentImpl> getAssignments(ProviderOptions options) {
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);
    
    String url = fullUrl(providerData, StringUtils.replace(COLLECTION_URI, "{ID}", options.getCourseId()));
    ResponseEntity<SakaiAssignmentCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiAssignmentCollection.class);
    return messageResponse.getBody().getAssignment_collection();
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
