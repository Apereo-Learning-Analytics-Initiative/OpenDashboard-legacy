/**
 * 
 */
package od.providers.sakai;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
@Component
public class BaseSakaiProvider {

  protected RestTemplate restTemplate = new RestTemplate();

  @Value("${sakai.host}")
  protected String sakaiHost;
  @Value("${sakai.username}")
  protected String sakaiUsername;
  @Value("${sakai.password}")
  protected String sakaiPassword;

  protected String getSakaiSession(String host) {

    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("_username", sakaiUsername);
    parameters.add("_password", sakaiPassword);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(
        parameters, null);
    
    String sakai_host = null;
    if (StringUtils.isNotBlank(host)) {
      sakai_host = host;
    }
    else {
      sakai_host = sakaiHost;
    }

    return restTemplate.postForObject(sakaiHost + "/direct/session", entity,
        String.class);
  }
  
  protected String fullUrl(String host, String uri) {
    
    if (StringUtils.isBlank(host)) {
      host = sakaiHost;
    }
    return host + uri;
  }

}
