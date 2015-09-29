/**
 * 
 */
package od;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TenantService {
  private String tenant;
  Map<String, String> tenantKeyMap;
  
  @PostConstruct
  public void init() {
    // TODO - eventually moved to a singleton
    tenantKeyMap = new HashMap<String, String>();
    tenantKeyMap.put("opendash", "tenant1");
  }

  public String getTenant() {
    String mappedValue = tenantKeyMap.get(tenant);
    if (StringUtils.isNotBlank(mappedValue)) {
      return mappedValue;
    }
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }
}
