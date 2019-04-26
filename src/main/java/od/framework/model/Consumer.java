/**
 * 
 */
package od.framework.model;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author ggilbert
 *
 */
public class Consumer extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
  private String name;
  @Indexed(unique=true)
  private String oauthConsumerKey;
  private String oauthConsumerSecret;
  
  public String getOauthConsumerKey() {
    return oauthConsumerKey;
  }
  public void setOauthConsumerKey(String oauthConsumerKey) {
    this.oauthConsumerKey = oauthConsumerKey;
  }
  public String getOauthConsumerSecret() {
    return oauthConsumerSecret;
  }
  public void setOauthConsumerSecret(String oauthConsumerSecret) {
    this.oauthConsumerSecret = oauthConsumerSecret;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  @Override
  public String toString() {
    return "Consumer [name=" + name + ", oauthConsumerKey=" + oauthConsumerKey + ", oauthConsumerSecret=" + oauthConsumerSecret + "]";
  }

}
