/**
 * 
 */
package od.providers.course.sakai;

import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.CourseImpl;

/**
 * @author ggilbert
 *
 */
public class SakaiSiteCollection extends OpenDashboardModel {
  
  private static final long serialVersionUID = 1L;

  public SakaiSiteCollection () {}
  
  private List<CourseImpl> site_collection;

  public List<CourseImpl> getSite_collection() {
    return site_collection;
  }

  public void setSite_collection(List<CourseImpl> site_collection) {
    this.site_collection = site_collection;
  }
}
