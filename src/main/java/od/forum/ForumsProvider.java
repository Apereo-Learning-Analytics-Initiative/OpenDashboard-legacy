/**
 * 
 */
package od.forum;

import java.util.Map;
import java.util.Set;

import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface ForumsProvider {
	Set<Forum> getForums(Map<String,String> options) throws ProviderException;
}
