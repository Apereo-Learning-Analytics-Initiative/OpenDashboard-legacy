/**
 * 
 */
package od.providers.assignments;

import java.util.Set;

import od.model.assignments.Assignment;
import od.model.roster.Member;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface AssignmentsProvider {
	Set<Assignment> getAssignments(String url) throws ProviderException;
}
