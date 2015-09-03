/**
 * 
 */
package od.providers.outcomes.sakai;

import java.util.ArrayList;
import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.LineItemImpl;

/**
 * @author ggilbert
 *
 */
public class SakaiGradebook extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;

  private String courseId;
  private String averageCourseGrade;
  private List<SakaiGradebookItem> items;
  
  public String getCourseId() {
    return courseId;
  }
  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }
  public String getAverageCourseGrade() {
    return averageCourseGrade;
  }
  public void setAverageCourseGrade(String averageCourseGrade) {
    this.averageCourseGrade = averageCourseGrade;
  }
  public List<SakaiGradebookItem> getItems() {
    return items;
  }
  public void setItems(List<SakaiGradebookItem> items) {
    this.items = items;
  }
  @Override
  public String toString() {
    return "SakaiGradebook [courseId=" + courseId + ", averageCourseGrade=" + averageCourseGrade + ", items=" + items + ", id=" + id + "]";
  }
  
  public List<LineItemImpl> toLineItems() {
    List<LineItemImpl> lineItems = null;
    
    if (this.items != null && !this.items.isEmpty()) {
      lineItems = new ArrayList<LineItemImpl>();
      for (SakaiGradebookItem item : this.items) {
        LineItemImpl lineItem = item.toLineItem();
        lineItem.setContext(this.courseId);
        lineItems.add(lineItem);
      }
    }
    
    return lineItems;
  }
}
