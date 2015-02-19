/**
 * 
 */
package od.providers.outcomes;

import java.util.Map;
import java.util.Set;

import od.model.outcomes.LineItem;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface OutcomesProvider {
	Set<LineItem> getOutcomesForCourse(Map<String, String> options, String courseId) throws ProviderException;
}
