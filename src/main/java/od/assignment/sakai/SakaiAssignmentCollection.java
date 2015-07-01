/**
 * 
 */
package od.assignment.sakai;

import java.util.List;

import od.assignment.Assignment;
import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class SakaiAssignmentCollection extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  private List<Assignment> assignment_collection;

  public List<Assignment> getAssignment_collection() {
    return assignment_collection;
  }

  public void setAssignment_collection(List<Assignment> assignment_collection) {
    this.assignment_collection = assignment_collection;
  }

}
