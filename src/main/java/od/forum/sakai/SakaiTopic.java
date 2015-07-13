/**
 * 
 */
package od.forum.sakai;

import od.forum.Topic;
import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class SakaiTopic extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  private String messagesCount;
  private String topicId;
  private String topicTitle;
  
  public String getMessagesCount() {
    return messagesCount;
  }
  public void setMessagesCount(String messagesCount) {
    this.messagesCount = messagesCount;
  }
  public String getTopicId() {
    return topicId;
  }
  public void setTopicId(String topicId) {
    this.topicId = topicId;
  }
  public String getTopicTitle() {
    return topicTitle;
  }
  public void setTopicTitle(String topicTitle) {
    this.topicTitle = topicTitle;
  }
  
  public Topic toTopic() {
    Topic topic = new Topic();
    topic.setCount(this.messagesCount);
    topic.setId(this.topicId);
    topic.setTitle(this.topicTitle);
    
    return topic;
  }

}
