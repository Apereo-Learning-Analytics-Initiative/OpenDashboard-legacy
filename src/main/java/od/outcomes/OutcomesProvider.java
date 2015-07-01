/**
 * 
 */
package od.outcomes;

import java.util.List;
import java.util.Map;

import od.providers.ProviderException;
import od.providers.ProviderOptions;

/**
 * @author ggilbert
 *
 */
public interface OutcomesProvider {
	List<LineItem> getOutcomesForCourse(ProviderOptions options) throws ProviderException;
}
