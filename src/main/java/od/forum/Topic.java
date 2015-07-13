/**
 * 
 */
package od.forum;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Topic extends OpenDashboardModel {

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
