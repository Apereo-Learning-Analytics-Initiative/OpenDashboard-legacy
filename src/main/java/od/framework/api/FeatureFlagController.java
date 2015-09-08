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
@ConfigurationProperties("")
@RestController
public class FeatureFlagController {
  
  private final Map<String, Object> features = new HashMap<String, Object>();
  
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
