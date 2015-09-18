/**
 * 
 */
package od.providers;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

/**
 * @author ggilbert
 *
 */
public class ProviderData implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id private String id;
  private String providerType;
  private String providerKey;
  private List<ProviderDataOption> options;

  public String getId() {
      return id;
  }
  public void setId(String id) {
      this.id = id;
  }

  public String getProviderType() {
    return providerType;
  }
  public void setProviderType(String providerType) {
    this.providerType = providerType;
  }
  public String getProviderKey() {
    return providerKey;
  }
  public void setProviderKey(String providerKey) {
    this.providerKey = providerKey;
  }
  public List<ProviderDataOption> getOptions() {
    return options;
  }
  public void setOptions(List<ProviderDataOption> options) {
    this.options = options;
  }
  
  public String findValueForKey(String key) {
    String value = null;
    
    if (options != null && !options.isEmpty()) {
      for (ProviderDataOption pdo : options) {
        if (key.equals(pdo.getKey())) {
          value = pdo.getValue();
        }
      }
    }
    
    return value;
  }
  
  @Override
  public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }


}
