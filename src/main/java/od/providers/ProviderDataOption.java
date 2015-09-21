/**
 * 
 */
package od.providers;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author ggilbert
 *
 */
public class ProviderDataOption implements Serializable {
  private static final long serialVersionUID = 1L;
  private String key;
  private String value;
  private boolean required;
  private boolean encrypt;
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public boolean isRequired() {
    return required;
  }
  public void setRequired(boolean required) {
    this.required = required;
  }
  public boolean isEncrypt() {
    return encrypt;
  }
  public void setEncrypt(boolean encrypt) {
    this.encrypt = encrypt;
  }

  @Override
  public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
