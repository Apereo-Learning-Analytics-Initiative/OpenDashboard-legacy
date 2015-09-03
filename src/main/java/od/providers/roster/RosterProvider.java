/**
 * 
 */
package od.providers.roster;

import java.util.Set;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.Member;

/**
 * @author ggilbert
 *
 */
public interface RosterProvider extends Provider {
  static final String DEFAULT = "roster_basiclis";
	Set<Member> getRoster(ProviderOptions options) throws ProviderException;
}
