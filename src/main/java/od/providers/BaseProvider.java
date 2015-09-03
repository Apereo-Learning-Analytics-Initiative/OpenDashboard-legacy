/**
 * 
 */
package od.providers;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

/**
 * @author ggilbert
 *
 */
public abstract class BaseProvider {
  
  protected HttpHeaders createHeadersWithBasicAuth(final String username, final String password){
    return new HttpHeaders(){
       {
          String auth = username + ":" + password;
          byte[] encodedAuth = Base64.encodeBase64( 
             auth.getBytes(Charset.forName("US-ASCII")) );
          String authHeader = "Basic " + new String( encodedAuth );
          set( "Authorization", authHeader );
       }
    };
  }
  
  protected String getUrl(String url, String path, Pageable pageable) {
    
    if (!url.endsWith("/")) {
      url = url + "/";
    }
    
    url = url + path;
    
    if (pageable != null) {
      int number = pageable.getPageNumber();
      int size = pageable.getPageSize();
      if (!url.endsWith("?")) {
        url = url + "?";
      }
      
      url = url + "&page="+number+"&limit="+size;
    }
    
    return url;
  }

  
}
