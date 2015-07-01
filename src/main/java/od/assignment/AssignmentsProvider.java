/**
 * 
 */
package od.assignment;

import java.util.List;

import od.providers.ProviderException;
import od.providers.ProviderOptions;

/**
 * @author ggilbert
 *
 */
public interface AssignmentsProvider {
	List<Assignment> getAssignments(ProviderOptions options) throws ProviderException;
}
