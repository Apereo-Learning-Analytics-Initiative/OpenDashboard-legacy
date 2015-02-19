/**
 * 
 */
package od.providers.demographics;

import java.util.Map;
import java.util.Set;

import od.model.roster.Demographic;
import od.providers.ProviderException;

/**
 * @author ggilbert
 *
 */
public interface DemographicsProvider {
	Set<Demographic> getDemographics(Map<String, String> options) throws ProviderException;
	Demographic getDemographicsForUser(Map<String, String> options, String userId) throws ProviderException;
}
