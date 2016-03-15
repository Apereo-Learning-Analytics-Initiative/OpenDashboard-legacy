/**
 * 
 */
package od.framework.model;

import java.net.URL;
import java.util.Set;

/**
 * @author ggilbert
 *
 */
public class Tenant extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
  private String name;
  private URL idpEndpoint;
  private Set<Consumer> consumers;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public URL getIdpEndpoint() {
    return idpEndpoint;
  }
  public void setIdpEndpoint(URL idpEndpoint) {
    this.idpEndpoint = idpEndpoint;
  }
  public Set<Consumer> getConsumers() {
    return consumers;
  }
  public void setConsumers(Set<Consumer> consumers) {
    this.consumers = consumers;
  }
}
