/**
 * 
 */
package od.providers.api;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderData;
import od.providers.ProviderService;
import od.repository.ProviderDataRepositoryInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class ProviderController {
  @Autowired private ProviderService providerService;
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;
  
  @RequestMapping(value = {"/api/providers/{type}"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public List<Provider> providersByType(@PathVariable("type") final String type) {
    return providerService.getProvidersByType(type);
  }
  
  @RequestMapping(value = {"/api/providers/{type}/{key}"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public Provider providerByTypeAndKey(@PathVariable("type") final String type, @PathVariable("key") final String key) {
    return providerService.getProviderByTypeAndKey(type,key);
  }

  @RequestMapping(value = "/api/providerdata/{type}/{key}", method = RequestMethod.PUT, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public ProviderData update(@RequestBody ProviderData providerData) {
     return providerDataRepositoryInterface.save(providerData);
  }
  
  @RequestMapping(value = "/api/providerdata/{type}/{key}", method = RequestMethod.DELETE, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public boolean delete(@RequestBody ProviderData providerData) {
     providerDataRepositoryInterface.delete(providerData);
     return true;
  }

  @RequestMapping(value = "/api/providerdata", method = RequestMethod.POST, 
      produces = "application/json;charset=utf-8", consumes = "application/json")
  public ProviderData create(@RequestBody ProviderData providerData) {
     return providerDataRepositoryInterface.save(providerData);
  }

  @RequestMapping(value = {"/api/providerdata/{type}"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public List<ProviderData> providerDataByType(@PathVariable("type") final String type) {
    return providerDataRepositoryInterface.findByProviderType(type);
  }
  
  @RequestMapping(value = {"/api/providerdata/{type}/{key}"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public ProviderData provideDataByTypeAndKey(@PathVariable("type") final String type, @PathVariable("key") final String key) {
    return providerDataRepositoryInterface.findByProviderKey(key);
  }

}
