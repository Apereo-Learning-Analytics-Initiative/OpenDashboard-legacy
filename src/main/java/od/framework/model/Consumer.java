/**
 * 
 */
package od.framework.model;

/**
 * @author ggilbert
 *
 */
public class Consumer extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
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

}
