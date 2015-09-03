package org.apereo.lai.impl;

import java.util.List;

import org.apereo.lai.Forum;

import od.framework.model.OpenDashboardModel;

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
