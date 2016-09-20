/**
 * 
 */
package od.providers.course.learninglocker;

import java.io.Serializable;

import od.providers.learninglocker.LearningLockerEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LearningLockerVLEModuleMap extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 2010056979657862548L;
  
  @JsonCreator
  public LearningLockerVLEModuleMap(
      @JsonProperty("_id") String id, 
      @JsonProperty("VLE_MOD_ID") String vleModId, 
      @JsonProperty("MOD_INSTANCE_ID") String moduleInstanceId) {
    super();
    this.id = id;
    this.vleModId = vleModId;
    this.moduleInstanceId = moduleInstanceId;
  }
  
  private String id;
  private String vleModId;
  private String moduleInstanceId;

  public String getId() {
    return id;
  }
  public String getVleModId() {
    return vleModId;
  }
  public String getModuleInstanceId() {
    return moduleInstanceId;
  }
  @Override
  public String toString() {
    return "LearningLockerVLEModuleMap [id=" + id + ", vleModId=" + vleModId + ", moduleInstanceId=" + moduleInstanceId + "]";
  }
}
