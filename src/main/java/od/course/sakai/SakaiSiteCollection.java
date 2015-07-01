/**
 * 
 */
package od.course.sakai;

import java.util.List;

import od.course.Course;
import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class SakaiSiteCollection extends OpenDashboardModel {
  
  private static final long serialVersionUID = 1L;

  public SakaiSiteCollection () {}
  
  private List<Course> site_collection;

  public List<Course> getSite_collection() {
    return site_collection;
  }

  public void setSite_collection(List<Course> site_collection) {
    this.site_collection = site_collection;
  }
}
