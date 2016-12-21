/**
 * 
 */
package od.providers.lineitem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.matthews.MatthewsClient;
import od.providers.matthews.MatthewsProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.oneroster.LineItem;


/**
 * @author ggilbert
 *
 */
@Component("lineitem_matthews")
public class MatthewsLineItemProvider extends MatthewsProvider implements LineItemProvider {
  
  private static final Logger log = LoggerFactory.getLogger(MatthewsLineItemProvider.class);
  
  private static final String KEY = "lineitem_matthews";
  private static final String BASE = "MATTHEWS_LINEITEMS";
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
  public Set<LineItem> getLineItemsForClass(ProviderData providerData, String classSourcedId) throws ProviderException {
    String endpoint = providerData.findValueForKey("base_url").concat("/api/classes/").concat(classSourcedId).concat("/lineitems");
    MatthewsClient mc = new MatthewsClient(providerData.findValueForKey("base_url"), providerData.findValueForKey("key"), providerData.findValueForKey("secret"));

    RestTemplate restTemplate = mc.getRestTemplate();
    HttpHeaders headers = mc.getHeaders();
    
    ResponseEntity<LineItem[]> response 
      = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(headers), LineItem[].class);
    
    LineItem [] lineItems = response.getBody();
    
    return new HashSet<>(Arrays.asList(lineItems));
  }

}
