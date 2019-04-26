/**
 * 
 */
package od.providers.learninglocker;

import java.io.Serializable;

import od.providers.course.learninglocker.LearningLockerModuleInstance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
public class LearningLockerStudentModuleInstance extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String MOD_INSTANCE_ID;
  private String STUDENT_ID;
  private LearningLockerStudent student;
  private LearningLockerModuleInstance modInstance;

  @JsonCreator
  public LearningLockerStudentModuleInstance(@JsonProperty("STUDENT_ID") String sTUDENT_ID, 
      @JsonProperty("student") LearningLockerStudent student,
      @JsonProperty("MOD_INSTANCE_ID") String MOD_INSTANCE_ID,
      @JsonProperty("moduleInstance") LearningLockerModuleInstance learningLockerModuleInstance) {
    super();
    this.STUDENT_ID = sTUDENT_ID;
    this.student = student;
    this.MOD_INSTANCE_ID = MOD_INSTANCE_ID;
    this.modInstance = learningLockerModuleInstance;
  }

  public String getSTUDENT_ID() {
    return STUDENT_ID;
  }

  public LearningLockerStudent getStudent() {
    return student;
  }

  public String getMOD_INSTANCE_ID() {
    return MOD_INSTANCE_ID;
  }

  public LearningLockerModuleInstance getModuleInstance() {
    return modInstance;
  }

}
