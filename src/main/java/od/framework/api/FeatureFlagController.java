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
package od.framework.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@ConfigurationProperties()
@RestController
public class FeatureFlagController {
  
  private final Map<String, Object> features = new HashMap<>();
  
  public Map<String, Object> getFeatures() {
    return features;
  }

  @RequestMapping(value = {"/features"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public Map<String,Object> features() {
    return features;
  }
  
  @RequestMapping(value = {"/features/{key}"}, method = RequestMethod.GET, produces="application/json;charset=utf-8")
  public Map<String,Object> featuresByKey(@PathVariable("key") final String key) {
    Object value = features.get(key);
    return Collections.singletonMap(key, value);
  }

}
