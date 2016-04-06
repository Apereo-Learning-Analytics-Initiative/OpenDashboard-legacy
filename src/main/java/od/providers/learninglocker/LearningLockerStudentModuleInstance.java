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
public class LearningLockerStudentModuleInstance extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String STUDENT_ID;

  @JsonCreator
  public LearningLockerStudentModuleInstance(@JsonProperty("STUDENT_ID") String sTUDENT_ID) {
    super();
    STUDENT_ID = sTUDENT_ID;
  }

  public String getSTUDENT_ID() {
    return STUDENT_ID;
  }

}
