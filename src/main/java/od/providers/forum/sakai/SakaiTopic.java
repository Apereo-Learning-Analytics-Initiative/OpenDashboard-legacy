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

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.TopicImpl;

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
  
  public TopicImpl toTopic() {
    TopicImpl topic = new TopicImpl();
    topic.setCount(this.messagesCount);
    topic.setId(this.topicId);
    topic.setTitle(this.topicTitle);
    
    return topic;
  }

}
