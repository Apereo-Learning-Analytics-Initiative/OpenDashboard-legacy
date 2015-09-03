/**
 * 
 */
package od.providers.forum;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.impl.ForumImpl;
import org.apereo.lai.impl.MessageImpl;

/**
 * @author ggilbert
 *
 */
public interface ForumsProvider extends Provider {
  static final String DEFAULT = "forums_sakai";
	List<ForumImpl> getForums(ProviderOptions options) throws ProviderException;
	List<MessageImpl> getMessages(ProviderOptions options, String topicId) throws ProviderException;
}
