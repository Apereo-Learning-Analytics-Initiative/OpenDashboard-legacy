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
public class SakaiTopicCollection extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  private List<SakaiForum> topic_collection;
  
  public List<SakaiForum> getTopic_collection() {
    return topic_collection;
  }
  public void setTopic_collection(List<SakaiForum> topic_collection) {
    this.topic_collection = topic_collection;
  }
}
