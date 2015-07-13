/**
 * 
 */
package od.forum.sakai;

import java.util.ArrayList;
import java.util.List;

import od.forum.Forum;
import od.forum.ForumsProvider;
import od.forum.Message;
import od.providers.BaseSakaiProvider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class SakaiForumsProvider extends BaseSakaiProvider implements ForumsProvider {

  private static final Logger log = LoggerFactory.getLogger(SakaiForumsProvider.class);
  private final String COLLECTION_URI = "/direct/topic/site/{ID}.json";
  private final String MESSAGES_URI = "/direct/forum_message/topic/{ID}.json";


  @Override
  public List<Forum> getForums(ProviderOptions options) {
    
    List<Forum> f = null;

    String url = fullUrl(options.getStrategyHost(), StringUtils.replace(COLLECTION_URI, "{ID}", options.getCourseId()));
    ResponseEntity<SakaiTopicCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiTopicCollection.class);
    List<SakaiForum> forums = messageResponse.getBody().getTopic_collection();
    
    if (forums != null && !forums.isEmpty()) {
      f = new ArrayList<Forum>();
      for (SakaiForum sakaiForum : forums) {
        f.add(sakaiForum.toForum());
      }
    }
    
    return f;
  }


  @Override
  public List<Message> getMessages(ProviderOptions options, final String topicId) throws ProviderException {
    List<Message> m = null;

    String url = fullUrl(options.getStrategyHost(), StringUtils.replace(MESSAGES_URI, "{ID}", topicId));
    ResponseEntity<SakaiTopicMessageCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), SakaiTopicMessageCollection.class);
    List<SakaiTopicMessage> messages = messageResponse.getBody().getForum_message_collection();
    
    if (messages != null && !messages.isEmpty()) {
      m = new ArrayList<Message>();
      for (SakaiTopicMessage sakaiTopicMessage : messages) {
        m.add(sakaiTopicMessage.toMessage());
      }
    }
    
    return m;
  }

}
