/**
 * 
 */
package od.outcomes.sakai;

import od.model.OpenDashboardModel;
import od.outcomes.Result;

/**
 * @author ggilbert
 *
 */
public class SakaiGradebookItemScore extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;
  
  private String grade;
  private Long recorded;
  private String itemId;
  private String userId;
  private String comment;
  
  public String getGrade() {
    return grade;
  }
  public void setGrade(String grade) {
    this.grade = grade;
  }
  public Long getRecorded() {
    return recorded;
  }
  public void setRecorded(Long recorded) {
    this.recorded = recorded;
  }
  public String getItemId() {
    return itemId;
  }
  public void setItemId(String itemId) {
    this.itemId = itemId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
  @Override
  public String toString() {
    return "SakaiGradebookItemScore [grade=" + grade + ", recorded=" + recorded + ", itemId=" + itemId + ", userId=" + userId + ", comment="
        + comment + ", id=" + id + "]";
  }
  
  public Result toResult() {
    Result result = new Result();
    result.setId(this.id);
    result.setComments(this.comment);
    result.setUserId(this.userId);
    result.setGrade(this.grade);
    return result;
  }

}
