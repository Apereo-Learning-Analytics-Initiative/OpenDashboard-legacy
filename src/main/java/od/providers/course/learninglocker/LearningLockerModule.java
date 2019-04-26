/**
 * 
 */
package od.providers.course.learninglocker;

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
public class LearningLockerModule extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 7968816755892214598L;
  
  @JsonCreator
  public LearningLockerModule(@JsonProperty("_id") String id, 
      @JsonProperty("CREDITS") int credits, 
      @JsonProperty("MOD_ID") String modId, 
      @JsonProperty("MOD_NAME") String modName,
      @JsonProperty("INSTITUTION_ID") String institutionId,
      @JsonProperty("createdAt") Date createdAt, 
      @JsonProperty("updatedAt") Date updatedAt) {
    super();
    this.id = id;
    this.credits = credits;
    this.modId = modId;
    this.modName = modName;
    this.institutionId = institutionId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
  private String id;
  private int credits;
  private String modId;
  private String modName;

  public String getId() {
    return id;
  }
  public int getCredits() {
    return credits;
  }
  public String getModId() {
    return modId;
  }
  public String getModName() {
    return modName;
  }
  @Override
  public String toString() {
    return "JiscModule [id=" + id + ", credits=" + credits + ", modId=" + modId + ", modName=" + modName + ", createdAt=" + createdAt
        + ", updatedAt=" + updatedAt + "]";
  }
  

}
