/**
 * 
 */
package od.providers.lineitem;

import java.util.Set;

import od.providers.Provider;
import od.providers.ProviderData;
import od.providers.ProviderException;
import unicon.matthews.oneroster.LineItem;

/**
 * @author ggilbert
 *
 */
public interface LineItemProvider extends Provider {
  Set<LineItem> getLineItemsForClass(ProviderData providerData, String classSourcedId) throws ProviderException;
}
