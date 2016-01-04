/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.providers;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

/**
 * @author ggilbert
 *
 */
public abstract class BaseProvider implements Provider {
  
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
    
    if (!url.endsWith("/") && !path.startsWith("/")) {
      url = url + "/";
    }
    
    url = url + path;
    
    if (pageable != null) {
      int number = pageable.getPageNumber();
      int size = pageable.getPageSize();
      
      if (url.contains("?")) {
        String queryParams = StringUtils.substringAfterLast(url, "?");
        if (StringUtils.isNotBlank(queryParams)) {
          url = url + "&";
        }
      }
      else {
        url = url + "?";
      }
            
      url = url + "page="+number+"&limit="+size;
    }
    
    return url;
  }

  
}
