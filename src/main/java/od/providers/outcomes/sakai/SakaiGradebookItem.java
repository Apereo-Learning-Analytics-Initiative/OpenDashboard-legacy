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
import org.apereo.lai.impl.ResultImpl;

/**
 * @author ggilbert
 *
 */
public class SakaiGradebookItem extends OpenDashboardModel {

  private static final long serialVersionUID = 1L;

  private String name;
  private String type;
  private Double pointsPossible;
  private Long dueDate;
  private List<SakaiGradebookItemScore> scores;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Double getPointsPossible() {
    return pointsPossible;
  }
  public void setPointsPossible(Double pointsPossible) {
    this.pointsPossible = pointsPossible;
  }
  public Long getDueDate() {
    return dueDate;
  }
  public void setDueDate(Long dueDate) {
    this.dueDate = dueDate;
  }
  public List<SakaiGradebookItemScore> getScores() {
    return scores;
  }
  public void setScores(List<SakaiGradebookItemScore> scores) {
    this.scores = scores;
  }
  @Override
  public String toString() {
    return "SakaiGradebookItem [name=" + name + ", type=" + type + ", pointsPossible=" + pointsPossible + ", dueDate=" + dueDate + ", scores="
        + scores + ", id=" + id + "]";
  }
  
  public LineItemImpl toLineItem() {
    LineItemImpl lineItem = new LineItemImpl();
    lineItem.setId(this.id);
    lineItem.setMaximumScore(this.pointsPossible);
    lineItem.setTitle(this.name);
    lineItem.setType(this.type);
    
    List<ResultImpl> results = null;
    if (this.scores != null && !this.scores.isEmpty()) {
      results = new ArrayList<>();
      for (SakaiGradebookItemScore score : this.scores) {
        results.add(score.toResult());
      }
    }
    lineItem.setResults(results);
    
    return lineItem;
  }
}
