/**
 * 
 */
package od.outcomes.sakai;

import java.util.ArrayList;
import java.util.List;

import od.model.OpenDashboardModel;
import od.outcomes.LineItem;
import od.outcomes.Result;

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
  
  public LineItem toLineItem() {
    LineItem lineItem = new LineItem();
    lineItem.setId(this.id);
    lineItem.setMaximumScore(this.pointsPossible);
    lineItem.setTitle(this.name);
    lineItem.setType(this.type);
    
    List<Result> results = null;
    if (this.scores != null && !this.scores.isEmpty()) {
      results = new ArrayList<Result>();
      for (SakaiGradebookItemScore score : this.scores) {
        results.add(score.toResult());
      }
    }
    lineItem.setResults(results);
    
    return lineItem;
  }
}
