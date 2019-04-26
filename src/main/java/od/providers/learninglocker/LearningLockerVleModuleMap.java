/**
 * 
 */
package od.providers.learninglocker;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
public class LearningLockerVleModuleMap extends LearningLockerEntity implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String VLE_MOD_ID;
  private String MOD_INSTANCE_ID;
  
  @JsonCreator
  public LearningLockerVleModuleMap(@JsonProperty("VLE_MOD_ID") String vLE_MOD_ID, 
      @JsonProperty("MOD_INSTANCE_ID") String mOD_INSTANCE_ID) {
    super();
    VLE_MOD_ID = vLE_MOD_ID;
    MOD_INSTANCE_ID = mOD_INSTANCE_ID;
  }

  public String getVLE_MOD_ID() {
    return VLE_MOD_ID;
  }

  public String getMOD_INSTANCE_ID() {
    return MOD_INSTANCE_ID;
  }

  @Override
  public String toString() {
    return "LearningLockerVleModuleMap [VLE_MOD_ID=" + VLE_MOD_ID + ", MOD_INSTANCE_ID=" + MOD_INSTANCE_ID + "]";
  }

}
