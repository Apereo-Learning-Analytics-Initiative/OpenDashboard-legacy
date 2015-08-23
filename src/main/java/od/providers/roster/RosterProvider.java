/**
 * 
 */
package od.providers.roster;

import java.util.Set;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.roster.Member;

/**
 * @author ggilbert
 *
 */
public interface RosterProvider extends Provider {
	Set<Member> getRoster(ProviderOptions options) throws ProviderException;
}
