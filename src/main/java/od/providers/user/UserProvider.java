/**
 * 
 */
package od.providers.user;

import od.providers.Provider;
import od.providers.ProviderData;
import unicon.oneroster.User;

/**
 * @author ggilbert
 *
 */
public interface UserProvider extends Provider {
  User getUserBySourcedId(ProviderData providerData, String userSourcedId);
}
