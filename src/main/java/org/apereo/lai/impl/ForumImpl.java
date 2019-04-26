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
package org.apereo.lai.impl;

import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Forum;

public class ForumImpl extends OpenDashboardModel implements Forum {

  private static final long serialVersionUID = 1L;

  private String title;
  private List<TopicImpl> topics;

  /* (non-Javadoc)
   * @see org.apereo.lai.impl.Forum#getTitle()
   */
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /* (non-Javadoc)
   * @see org.apereo.lai.impl.Forum#getTopics()
   */
  public List<TopicImpl> getTopics() {
    return topics;
  }

  public void setTopics(List<TopicImpl> topics) {
    this.topics = topics;
  }

}
