/**
 * 
 */
package od.providers.learninglocker;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
public class LearningLockerStaffModuleInstance extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 1L;
    
  public LearningLockerStaffModuleInstance(
      @JsonProperty("_id") String id, 
      @JsonProperty("STAFF_ID") String staffId, 
      @JsonProperty("MOD_INSTANCE_ID") String modInstanceId) {
    super();
    this.id = id;
    this.staffId = staffId;
    this.modInstanceId = modInstanceId;
  }

    
  private String id;
  private String staffId;
  private String modInstanceId;

  public String getId() {
    return id;
  }
  public String getStaffId() {
    return staffId;
  }
  public String getModInstanceId() {
    return modInstanceId;
  }
  @Override
  public String toString() {
    return "LearningLockerStaffModuleInstance [id=" + id + ", staffId=" + staffId + ", modInstanceId=" + modInstanceId + "]";
  }

}
