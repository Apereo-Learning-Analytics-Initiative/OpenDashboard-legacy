/**
 * 
 */
package od.providers.outcomes.learninglocker;

import java.io.Serializable;
import java.util.Date;

import od.providers.learninglocker.LearningLockerEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class LearningLockerGrade extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 1557271939914361852L;
  
  @JsonCreator
  public LearningLockerGrade(@JsonProperty("_id") final String id, 
      @JsonProperty("STUDENT") final String studentId, 
      @JsonProperty("MODULE_INSTANCE") final String moduleInstanceId, 
      @JsonProperty("CATEGORY") final String category, 
      @JsonProperty("GRADEABLE_OBJECT") final String gradableObject, 
      @JsonProperty("MAX_POINTS") final Float maxPoints, 
      @JsonProperty("EARNED_POINTS") final Float earnedPoints,
      @JsonProperty("WEIGHT") final Float weight, 
      @JsonProperty("INSTITUTION_ID") String institutionId,
      @JsonProperty("createdAt") final Date createdAt, 
      @JsonProperty("updatedAt") final Date updatedAt) {
    super();
    this.id = id;
    this.studentId = studentId;
    this.moduleInstanceId = moduleInstanceId;
    this.category = category;
    this.gradableObject = gradableObject;
    this.maxPoints = maxPoints;
    this.earnedPoints = earnedPoints;
    this.weight = weight;
    this.institutionId = institutionId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
  
  private String id;
  private String studentId;
  private String moduleInstanceId;
  private String category;
  private String gradableObject;
  private Float maxPoints;
  private Float earnedPoints;
  private Float weight;

  public String getId() {
    return id;
  }
  public String getStudentId() {
    return studentId;
  }
  public String getModuleInstanceId() {
    return moduleInstanceId;
  }
  public String getCategory() {
    return category;
  }
  public String getGradableObject() {
    return gradableObject;
  }
  public Float getMaxPoints() {
    return maxPoints;
  }
  public Float getEarnedPoints() {
    return earnedPoints;
  }
  public Float getWeight() {
    return weight;
  }

  @Override
  public String toString() {
    return "JiscGrade [id=" + id + ", studentId=" + studentId + ", moduleInstanceId=" + moduleInstanceId + ", category=" + category
        + ", gradableObject=" + gradableObject + ", maxPoints=" + maxPoints + ", earnedPoints=" + earnedPoints + ", weight=" + weight
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
  }


}
