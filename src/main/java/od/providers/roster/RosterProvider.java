/**
 * 
 */
package od.providers.roster;

import java.util.Set;

import od.model.roster.Member;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface RosterProvider {
	Set<Member> getRoster(String url, String rosterIdentifier) throws ProviderException;
}
