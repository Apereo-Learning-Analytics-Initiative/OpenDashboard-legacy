/**
 * 
 */
package od.providers.user;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.matthews.MatthewsClient;
import od.providers.matthews.MatthewsProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.entity.UserMapping;
import unicon.matthews.oneroster.User;


/**
 * @author ggilbert
 *
 */
@Component("user_matthews")
public class MatthewsUserProvider extends MatthewsProvider implements UserProvider {

  private static final Logger log = LoggerFactory.getLogger(MatthewsUserProvider.class);
  
  private static final String KEY = "user_matthews";
  private static final String BASE = "MATTHEWS_USER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultMatthewsConfiguration();
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public User getUserBySourcedId(ProviderData providerData, String userSourcedId) {
    String endpoint = providerData.findValueForKey("base_url").concat("/api/users/").concat(userSourcedId);
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<User> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), User.class);
    
    User user = response.getBody();
    
    return user;
  }

  @Override
  public String getUserSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
    ProviderData providerData = tenant.findByKey(KEY);

    String endpoint = providerData.findValueForKey("base_url").concat("/api/users/mapping/").concat(externalId);
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<UserMapping> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserMapping.class);
    
    if (response.getStatusCode() == HttpStatus.NOT_FOUND 
        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR 
        || response.getBody() == null) {
      return null;
    }
    
    UserMapping userMapping = response.getBody();
    
    return userMapping.getUserSourcedId();
  }

}
