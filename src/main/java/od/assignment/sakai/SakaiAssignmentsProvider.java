/**
 * 
 */
package od.assignment.sakai;

import java.util.List;

import od.assignment.Assignment;
import od.assignment.AssignmentsProvider;
import od.providers.BaseSakaiProvider;
import od.providers.ProviderOptions;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author mflynn
 *
 */
@Component
public class SakaiAssignmentsProvider extends BaseSakaiProvider implements AssignmentsProvider {

  private static final Logger log = LoggerFactory.getLogger(SakaiAssignmentsProvider.class);
  private final String COLLECTION_URI = "/direct/assignment/site/{ID}.json";

  @Override
  public List<Assignment> getAssignments(ProviderOptions options) {

    String url = fullUrl(options.getStrategyHost(), StringUtils.replace(COLLECTION_URI, "{ID}", options.getCourseId()));
    ResponseEntity<SakaiAssignmentCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiAssignmentCollection.class);
    return messageResponse.getBody().getAssignment_collection();
  }

}
