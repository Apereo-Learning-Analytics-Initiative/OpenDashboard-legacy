/**
 * 
 */
package od.providers.config;

/**
 * @author ggilbert
 *
 */
abstract class KeyValueProviderConfigurationOption implements ProviderConfigurationOption {
  
  protected String key;
  protected String defaultValue;
  protected String type;
  protected boolean isRequired;

  public String getKey() {
    return key;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean isRequired() {
    return isRequired;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}
