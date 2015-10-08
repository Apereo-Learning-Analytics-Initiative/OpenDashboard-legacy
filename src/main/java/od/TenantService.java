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
