/**
 * 
 */
package org.apereo.lai.impl;

import org.apereo.lai.Course;

import od.framework.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class CourseImpl extends OpenDashboardModel implements Course {

  private static final long serialVersionUID = 1L;
  
  private String title;

  @Override
  public String toString() {
    return "Course [title=" + title + ", id=" + id + "]";
  }

  /* (non-Javadoc)
   * @see org.apereo.lai.impl.Course#getTitle()
   */
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
