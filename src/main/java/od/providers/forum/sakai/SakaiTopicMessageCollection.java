/**
 * 
 */
package od.providers.forum.sakai;

import java.util.List;

import od.framework.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class SakaiTopicMessageCollection extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;

  private List<SakaiTopicMessage> forum_message_collection;

  public List<SakaiTopicMessage> getForum_message_collection() {
    return forum_message_collection;
  }

  public void setForum_message_collection(List<SakaiTopicMessage> forum_message_collection) {
    this.forum_message_collection = forum_message_collection;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
