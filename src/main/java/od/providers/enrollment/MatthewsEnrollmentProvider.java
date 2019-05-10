/**
 * 
 */
package od.providers.enrollment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.matthews.MatthewsClient;
import od.providers.matthews.MatthewsProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.oneroster.Enrollment;


/**
 * @author ggilbert
 *
 */
@Component("roster_matthews")
public class MatthewsEnrollmentProvider extends MatthewsProvider implements EnrollmentProvider {

  private static final Logger log = LoggerFactory.getLogger(MatthewsEnrollmentProvider.class);

  private static final String KEY = "roster_matthews";
  private static final String BASE = "MATTHEWS_ROSTER";
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
  
  private Set<unicon.matthews.oneroster.Enrollment> fetch(String endpoint, ProviderData providerData) {
    
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));
    
    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<Enrollment[]> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), Enrollment[].class);
    
    Enrollment [] enrollments = response.getBody();
    
    return new HashSet<>(Arrays.asList(enrollments));
  }
 
  @Override
  public Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, boolean activeOnly) throws ProviderException {
    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/").concat(classSourcedId).concat("/enrollments");
    return fetch(endpoint, providerData);
  }

  @Override
  public Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId, boolean activeOnly) throws ProviderException {
    String endpoint = providerData.findValueForKey("base_url").concat("/api/users/").concat(userSourcedId).concat("/enrollments");
    return fetch(endpoint, providerData);
  }

  @Override
  public List<String> getUniqueUsersWithRole(ProviderData providerData, String role) throws ProviderException {
    String endpoint = providerData.findValueForKey("base_url").concat("/api/users/withrole/").concat(role).concat("/enrollments");
    
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));
    
    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<String[]> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), String[].class);
    
    String [] userIds = response.getBody();
    
    return new ArrayList<>(Arrays.asList(userIds));
  }
}
