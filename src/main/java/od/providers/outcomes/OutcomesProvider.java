/**
 * 
 */
package od.providers.outcomes;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.impl.LineItemImpl;

/**
 * @author ggilbert
 *
 */
public interface OutcomesProvider extends Provider {
  static final String DEFAULT = "outcomes_sakai";
	List<LineItemImpl> getOutcomesForCourse(ProviderOptions options) throws ProviderException;
}
