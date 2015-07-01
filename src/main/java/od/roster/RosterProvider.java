/**
 * 
 */
package od.roster;

import java.util.Set;

import od.providers.ProviderException;
import od.providers.ProviderOptions;

/**
 * @author ggilbert
 *
 */
public interface RosterProvider {
	Set<Member> getRoster(ProviderOptions options) throws ProviderException;
}
