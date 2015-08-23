/**
 * 
 */
package od.providers.config;

/**
 * @author ggilbert
 *
 */
public class KeyValueProviderConfigurationOption implements ProviderConfigurationOption {
  
  private String key;
  private String value;
  private String type;
  private boolean isRequired;

  public KeyValueProviderConfigurationOption(String key, String value, String type, boolean isRequired) {
    super();
    this.key = key;
    this.value = value;
    this.type = type;
    this.isRequired = isRequired;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean isRequired() {
    return isRequired;
  }

}
