/**
 * 
 */
package org.apereo.lai.impl;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Topic;

/**
 * @author ggilbert
 *
 */
public class TopicImpl extends OpenDashboardModel implements Topic {

  private static final long serialVersionUID = 1L;
  private String title;
  private String count;
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getCount() {
    return count;
  }
  public void setCount(String count) {
    this.count = count;
  }

}
