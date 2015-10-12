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

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.impl.ResultImpl;

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
  
  public ResultImpl toResult() {
    ResultImpl result = new ResultImpl();
    result.setId(this.id);
    result.setComments(this.comment);
    result.setUserId(this.userId);
    result.setGrade(this.grade);
    return result;
  }

}
