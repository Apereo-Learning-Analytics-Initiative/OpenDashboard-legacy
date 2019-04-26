/**
 * 
 */
package od.providers.learninglocker;

import java.io.Serializable;

import od.providers.course.learninglocker.LearningLockerModuleInstance;
import od.providers.course.learninglocker.LearningLockerStaff;

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
      @JsonProperty("MOD_INSTANCE_ID") String modInstanceId,
      @JsonProperty("moduleInstance") LearningLockerModuleInstance moduleInstance,
      @JsonProperty("staff") LearningLockerStaff staff) {
    super();
    this.id = id;
    this.staffId = staffId;
    this.modInstanceId = modInstanceId;
    this.moduleInstance = moduleInstance;
    this.staff = staff;
  }

    
  private String id;
  private String staffId;
  private String modInstanceId;
  private LearningLockerModuleInstance moduleInstance;
  private LearningLockerStaff staff;

  public String getId() {
    return id;
  }
  public String getStaffId() {
    return staffId;
  }
  public String getModInstanceId() {
    return modInstanceId;
  }
  public LearningLockerModuleInstance getModuleInstance() {
    return moduleInstance;
  }
  public LearningLockerStaff getStaff() {
    return staff;
  }
  @Override
  public String toString() {
    return "LearningLockerStaffModuleInstance [id=" + id + ", staffId=" + staffId + ", modInstanceId=" + modInstanceId + "]";
  }

}
