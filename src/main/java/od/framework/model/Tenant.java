/**
 * 
 */
package od.framework.model;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import od.providers.ProviderData;

/**
 * @author ggilbert
 *
 */
public class Tenant extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
  private String name;
  private String description;
  private URL idpEndpoint;
  private Set<Consumer> consumers;
  private Set<ProviderData> providerData;
  private Set<Dashboard> dashboards;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
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
  public Set<ProviderData> getProviderData() {
    return providerData;
  }
  public void setProviderData(Set<ProviderData> providerData) {
    this.providerData = providerData;
  }
  public Set<Dashboard> getDashboards() {
    return dashboards;
  }
  public void setDashboards(Set<Dashboard> dashboards) {
    this.dashboards = dashboards;
  }
  
  public ProviderData findByKey(String key) {
    ProviderData pd = null;
    if (providerData != null && !providerData.isEmpty()) {
      try {
        pd = providerData.stream().filter(p -> p.getProviderKey().equals(key)).findFirst().get();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    return pd;
  }

  public List<ProviderData> findByType(String type) {
    List<ProviderData> pd = null;
    if (providerData != null && !providerData.isEmpty()) {
      try {
        pd = providerData.stream().filter(p -> p.getProviderType().equals(type)).collect(Collectors.toList());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    return pd;
  }

  
  @Override
  public String toString() {
    return "Tenant [name=" + name + ", description=" + description + ", idpEndpoint=" + idpEndpoint + ", consumers=" + consumers + ", providerData="
        + providerData + ", dashboards=" + dashboards + "]";
  }
}
