/**
 * 
 */
package od.providers.assignment.sakai;

import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.AssignmentImpl;

/**
 * @author ggilbert
 *
 */
public class SakaiAssignmentCollection extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  private List<AssignmentImpl> assignment_collection;

  public List<AssignmentImpl> getAssignment_collection() {
    return assignment_collection;
  }

  public void setAssignment_collection(List<AssignmentImpl> assignment_collection) {
    this.assignment_collection = assignment_collection;
  }

}
