/**
 * 
 */
package od.providers;

/**
 * @author ggilbert
 *
 */
public class NoVLEModuleMapException extends ProviderException {

  private static final long serialVersionUID = -7795396536933469119L;
  
  private String contextId;

  public NoVLEModuleMapException(String contextId) {
    super();
    this.contextId = contextId;
  }

  public String getContextId() {
    return contextId;
  }

}
