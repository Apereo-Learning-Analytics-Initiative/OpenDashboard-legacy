/**
 * 
 */
package od.providers.forums;

import java.util.Set;

import od.model.assignments.Assignment;
import od.model.forums.Forum;
import od.model.roster.Member;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface ForumsProvider {
	Set<Forum> getForums(String url) throws ProviderException;
}
