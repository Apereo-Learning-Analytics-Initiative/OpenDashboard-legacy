/**
 * 
 */
package od.providers.outcomes;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import od.model.outcomes.LineItem;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
@Component
public class SakaiOutcomesProvider implements OutcomesProvider {

  /* (non-Javadoc)
   * @see od.providers.outcomes.OutcomesProvider#getOutcomesForCourse(java.util.Map, java.lang.String)
   */
  @Override
  public Set<LineItem> getOutcomesForCourse(Map<String, String> options,
      String courseId) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

}
