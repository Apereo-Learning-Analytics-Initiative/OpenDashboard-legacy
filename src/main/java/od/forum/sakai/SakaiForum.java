/**
 * 
 */
package od.forum.sakai;

import java.util.ArrayList;
import java.util.List;

import od.forum.Forum;
import od.forum.Topic;
import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class SakaiForum extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  private String forumId;
  private String forumTitle;
  private List<SakaiTopic> topics;
  
  public String getForumId() {
    return forumId;
  }
  public void setForumId(String forumId) {
    this.forumId = forumId;
  }
  public String getForumTitle() {
    return forumTitle;
  }
  public void setForumTitle(String forumTitle) {
    this.forumTitle = forumTitle;
  }
  public List<SakaiTopic> getTopics() {
    return topics;
  }
  public void setTopics(List<SakaiTopic> topics) {
    this.topics = topics;
  }
  
  public Forum toForum() {
    Forum forum = new Forum();
    forum.setId(this.forumId);
    forum.setTitle(this.forumTitle);
    
    if (this.topics != null && !this.topics.isEmpty()) {
      List<Topic> t = new ArrayList<Topic>();
      for (SakaiTopic sakaiTopic : this.topics) {
        Topic topic = sakaiTopic.toTopic();
        t.add(topic);
      }
      forum.setTopics(t);
    }
    
    return forum;
  }
 
}
