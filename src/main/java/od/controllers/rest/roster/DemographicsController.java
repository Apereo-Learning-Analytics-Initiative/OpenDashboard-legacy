/**
 * 
 */
package od.controllers.rest.roster;

import java.util.Collection;

import od.model.roster.Demographic;
import od.providers.ProviderException;
import od.providers.demographics.DemographicsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class DemographicsController {
	
	@Autowired private DemographicsProvider demographicsProvider;
	
	@RequestMapping(value = "/api/demographics", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public Collection<Demographic> all() throws ProviderException {
		return demographicsProvider.getDemographics(null);
	}

	@RequestMapping(value = "/api/demographics/{id}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public Demographic user(@PathVariable("id") final String user_id) throws ProviderException {
		return demographicsProvider.getDemographicsForUser(null,user_id);
	}


}
