/**
 * 
 */
package od.providers.config;

/**
 * @author ggilbert
 *
 */
public class TranslatableKeyValueConfigurationOptions extends KeyValueProviderConfigurationOption {

  private String label;
  private String translatableLabelKey;
  private boolean encrypt;
  
  public TranslatableKeyValueConfigurationOptions(String key, String defaultValue, String type, boolean isRequired, String label, String translatableLabelKey, boolean encrypt) {
    super();
    this.key = key;
    this.defaultValue = defaultValue;
    this.type = type;
    this.isRequired = isRequired;
    this.label = label;
    this.translatableLabelKey = translatableLabelKey;
    this.encrypt = encrypt;
  }
  
  public String getLabel() {
    return label;
  }
  public String getTranslatableLabelKey() {
    return translatableLabelKey;
  }

  public boolean isEncrypt() {
    return encrypt;
  }
}
