/**
 * 
 */
package od.course;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Course extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
  private String title;

  @Override
  public String toString() {
    return "Course [title=" + title + ", id=" + id + "]";
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
