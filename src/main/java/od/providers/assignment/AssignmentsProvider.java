/**
 * 
 */
package od.providers.assignment;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.impl.AssignmentImpl;

/**
 * @author ggilbert
 *
 */
public interface AssignmentsProvider extends Provider {
	List<AssignmentImpl> getAssignments(ProviderOptions options) throws ProviderException;
}
