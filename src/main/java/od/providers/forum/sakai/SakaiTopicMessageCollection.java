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
