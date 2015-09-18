/**
 * 
 */
package od.repository;

import java.util.List;

import od.providers.ProviderData;

/**
 * @author ggilbert
 *
 */
public interface ProviderDataRepositoryInterface {
  ProviderData save(final ProviderData providerData);
  void delete(final ProviderData providerData);
  ProviderData findByProviderKey(final String providerKey);
  List<ProviderData> findByProviderType(final String providerType);
}
