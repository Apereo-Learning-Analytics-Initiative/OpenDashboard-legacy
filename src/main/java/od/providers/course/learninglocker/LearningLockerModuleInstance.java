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
public class LearningLockerModuleInstance extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = -6673557019049965737L;
    
  @JsonCreator
  public LearningLockerModuleInstance(@JsonProperty("_id") String id, 
      @JsonProperty("MODULE") LearningLockerModule module, 
      @JsonProperty("ACADEMIC_YEAR") String academicYear, 
      @JsonProperty("LEVEL") int level, 
      @JsonProperty("MOD_ENDDATE") Date modEndDate, 
      @JsonProperty("PERIOD") int period, 
      @JsonProperty("MOD_STARTDATE") Date modStartDate, 
      @JsonProperty("OPTIONAL") int optional,
      @JsonProperty("MOD_INSTANCE_ID") String modInstanceId, 
      @JsonProperty("INSTITUTION_ID") String institutionId,
      @JsonProperty("createdAt") Date createdAt, 
      @JsonProperty("updatedAt") Date updatedAt) {
    super();
    this.id = id;
    this.module = module;
    this.academicYear = academicYear;
    this.level = level;
    this.modEndDate = modEndDate;
    this.period = period;
    this.modStartDate = modStartDate;
    this.optional = optional;
    this.modInstanceId = modInstanceId;
    this.institutionId = institutionId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
  
  private String id;
  private LearningLockerModule module;
  private String academicYear;
  private int level;
  private Date modEndDate;
  private int period;
  private Date modStartDate;
  private int optional;
  private String modInstanceId;

  public String getId() {
    return id;
  }
  public LearningLockerModule getModule() {
    return module;
  }
  public String getAcademicYear() {
    return academicYear;
  }
  public int getLevel() {
    return level;
  }
  public Date getModEndDate() {
    return modEndDate;
  }
  public int getPeriod() {
    return period;
  }
  public Date getModStartDate() {
    return modStartDate;
  }
  public int getOptional() {
    return optional;
  }
  public String getModInstanceId() {
    return modInstanceId;
  }

  @Override
  public String toString() {
    return "JiscModuleInstance [id=" + id + ", module=" + module + ", academicYear=" + academicYear + ", level=" + level + ", modEndDate="
        + modEndDate + ", period=" + period + ", modStartDate=" + modStartDate + ", optional=" + optional + ", modInstanceId=" + modInstanceId
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
  }

}
