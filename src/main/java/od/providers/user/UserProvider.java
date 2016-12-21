/**
 * 
 */
package od.providers.user;

import od.framework.model.Tenant;
import od.providers.Provider;
import od.providers.ProviderData;
import od.providers.ProviderException;
import unicon.matthews.oneroster.User;

/**
 * @author ggilbert
 *
 */
public interface UserProvider extends Provider {
  User getUserBySourcedId(ProviderData providerData, String userSourcedId);
  String getUserSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException;
}
