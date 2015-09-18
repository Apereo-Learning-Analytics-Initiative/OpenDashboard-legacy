/**
 * 
 */
package od.providers.config;

/**
 * @author ggilbert
 *
 */
public interface ProviderConfigurationOption {
  
  final static String TEXT_TYPE = "text";
  final static String PASSWORD_TYPE = "password";
  final static String URL_TYPE = "url";
  
  boolean isRequired();
  String getType();
  String getKey();
}
