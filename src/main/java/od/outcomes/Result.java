/**
 * 
 */
package od.outcomes;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Result extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String grade;
	private String comments;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
  public String getGrade() {
    return grade;
  }
  public void setGrade(String grade) {
    this.grade = grade;
  }
}
