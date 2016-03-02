/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.providers.forum.sakai;

import java.util.ArrayList;
import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.ForumImpl;
import org.apereo.lai.impl.TopicImpl;

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
  
  public ForumImpl toForum() {
    ForumImpl forum = new ForumImpl();
    forum.setId(this.forumId);
    forum.setTitle(this.forumTitle);
    
    if (this.topics != null && !this.topics.isEmpty()) {
      List<TopicImpl> t = new ArrayList<>();
      for (SakaiTopic sakaiTopic : this.topics) {
        TopicImpl topic = sakaiTopic.toTopic();
        t.add(topic);
      }
      forum.setTopics(t);
    }
    
    return forum;
  }
 
}
