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
      lineItems = new ArrayList<>();
      for (SakaiGradebookItem item : this.items) {
        LineItemImpl lineItem = item.toLineItem();
        lineItem.setContext(this.courseId);
        lineItems.add(lineItem);
      }
    }
    
    return lineItems;
  }
}
