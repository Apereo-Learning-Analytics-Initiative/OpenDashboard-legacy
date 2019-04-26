/**
 * 
 */
package od.providers.course.learninglocker;

import java.io.Serializable;

import od.providers.learninglocker.LearningLockerEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LearningLockerStaff extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = -2595892465321478898L;
    
  public LearningLockerStaff(
      @JsonProperty("_id") String id, 
      @JsonProperty("STAFF_ID") String staffId, 
      @JsonProperty("LAST_NAME") String lastName, 
      @JsonProperty("TITLE") String title, 
      @JsonProperty("FIRST_NAME") String firstName, 
      @JsonProperty("HESA_STAFF_ID") String hesaStaffId,
      @JsonProperty("PRIMARY_EMAIL_ADDRESS") String primaryEmailAddress) {
    super();
    this.id = id;
    this.staffId = staffId;
    this.lastName = lastName;
    this.title = title;
    this.firstName = firstName;
    this.hesaStaffId = hesaStaffId;
    this.primaryEmailAddress = primaryEmailAddress;
  }
  private String id;
  private String staffId;
  private String lastName;
  private String title;
  private String firstName;
  private String hesaStaffId;
  private String primaryEmailAddress;

  public String getId() {
    return id;
  }
  public String getStaffId() {
    return staffId;
  }
  public String getLastName() {
    return lastName;
  }
  public String getTitle() {
    return title;
  }
  public String getFirstName() {
    return firstName;
  }
  public String getHesaStaffId() {
    return hesaStaffId;
  }
  public String getPrimaryEmailAddress() {
    return primaryEmailAddress;
  }
  @Override
  public String toString() {
    return "LearningLockerStaff [id=" + id + ", staffId=" + staffId + ", lastName=" + lastName + ", title=" + title + ", firstName=" + firstName
        + ", hesaStaffId=" + hesaStaffId + ", primaryEmailAddress=" + primaryEmailAddress + "]";
  }

}
