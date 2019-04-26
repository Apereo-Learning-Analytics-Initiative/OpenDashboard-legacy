/**
 * 
 */
package od.providers.matthews;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author ggilbert
 *
 */
public class MatthewsClient {

  private RestTemplate restTemplate;
  
  private String key;
  private String secret;
  private String baseUrl;
  
  private static final String LOGIN_URL = "/api/auth/login";
  
  public MatthewsClient(String baseUrl, String key, String secret) {
    this.baseUrl = baseUrl;
    
    if (StringUtils.endsWith(this.baseUrl, "/")) {
      this.baseUrl = StringUtils.substringBeforeLast(this.baseUrl, "/");
    }
    
    this.key = key;
    this.secret = secret;
    
    this.restTemplate = new RestTemplate();
  }
  
  public RestTemplate getRestTemplate() {
    return this.restTemplate;
  }
  
  public HttpHeaders getHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.add("X-Requested-With", "XMLHttpRequest");
    httpHeaders.add("Authorization", "Bearer "+getToken());
    
    return httpHeaders;
  }
  
  @SuppressWarnings("rawtypes")
  private String getToken() {
    
    JsonObject request = new JsonObject();
    request.add("username", new JsonPrimitive(this.key));
    request.add("password", new JsonPrimitive(this.secret));

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.add("X-Requested-With", "XMLHttpRequest");
    HttpEntity<String> entity = new HttpEntity<String>(request.toString(), httpHeaders);
    
    ResponseEntity<Map> loginResponse 
      = restTemplate
        .exchange(this.baseUrl + LOGIN_URL, 
            HttpMethod.POST, 
            entity, 
            Map.class);
    return (String)loginResponse.getBody().get("token");
  }
  
}
