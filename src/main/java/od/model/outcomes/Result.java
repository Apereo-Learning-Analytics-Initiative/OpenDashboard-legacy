/**
 * 
 */
package od.model.outcomes;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Result extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;

	private String id;
	private String user_id;
	private Double score;
	private String comments;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
