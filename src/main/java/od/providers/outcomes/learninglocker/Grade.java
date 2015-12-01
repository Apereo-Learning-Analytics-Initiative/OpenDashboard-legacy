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
package od.providers.outcomes.learninglocker;

import java.io.Serializable;
import java.util.Date;

import org.apereo.lai.impl.LineItemImpl;
import org.apereo.lai.impl.ResultImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Grade implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @JsonProperty("_id")
  private String id;
  
  @JsonProperty("STUDENT")
  private String studentId;
  
  @JsonProperty("MODULE_INSTANCE")
  private String moduleInstanceId;
  
  @JsonProperty("CATEGORY")
  private int category;
  
  @JsonProperty("GRADEABLE_OBJECT")
  private String gradableObject;
  
  @JsonProperty("MAX_POINTS")
  private int maxPoints;
  
  @JsonProperty("EARNED_POINTS")
  private int earnedPoints;
  
  @JsonProperty("WEIGHT")
  private int weight;
  
  @JsonProperty("createdAt")
  private Date createdAt;
  
  @JsonProperty("updatedAt")
  private Date updatedAt;
  
  public LineItemImpl toLineItem() {
    LineItemImpl lineItem = new LineItemImpl();
    lineItem.setContext(this.moduleInstanceId);
    lineItem.setMaximumScore(new Double(this.maxPoints));
    lineItem.setTitle(this.gradableObject);
    return lineItem;
  }
  
  public ResultImpl toResult() {
    ResultImpl result = new ResultImpl();
    result.setUserId(this.studentId);
    result.setGrade(Integer.toString(this.earnedPoints));
    result.setId(this.id);
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getModuleInstanceId() {
    return moduleInstanceId;
  }

  public void setModuleInstanceId(String moduleInstanceId) {
    this.moduleInstanceId = moduleInstanceId;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public String getGradableObject() {
    return gradableObject;
  }

  public void setGradableObject(String gradableObject) {
    this.gradableObject = gradableObject;
  }

  public int getMaxPoints() {
    return maxPoints;
  }

  public void setMaxPoints(int maxPoints) {
    this.maxPoints = maxPoints;
  }

  public int getEarnedPoints() {
    return earnedPoints;
  }

  public void setEarnedPoints(int earnedPoints) {
    this.earnedPoints = earnedPoints;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Grade [id=" + id + ", studentId=" + studentId + ", moduleInstanceId=" + moduleInstanceId + ", category=" + category + ", gradableObject="
        + gradableObject + ", maxPoints=" + maxPoints + ", earnedPoints=" + earnedPoints + ", weight=" + weight + ", createdAt=" + createdAt
        + ", updatedAt=" + updatedAt + "]";
  }

}
