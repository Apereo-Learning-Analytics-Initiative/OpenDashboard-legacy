/**
 * 
 */
package od.forum;

import java.util.List;

import od.providers.ProviderException;
import od.providers.ProviderOptions;

/**
 * @author ggilbert
 *
 */
public interface ForumsProvider {
	List<Forum> getForums(ProviderOptions options) throws ProviderException;
	List<Message> getMessages(ProviderOptions options, String topicId) throws ProviderException;
}
