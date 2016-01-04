/**
 * 
 */
package od.providers.learninglocker;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LearningLockerStudent extends LearningLockerEntity implements Serializable {

  private static final long serialVersionUID = 5782551266135994636L;
  
  public LearningLockerStudent() {}
  
  @JsonCreator
  public LearningLockerStudent(
      @JsonProperty("_id") String id, 
      @JsonProperty("MOBILE_PHONE") String mobilePhone, 
      @JsonProperty("FIRST_NAME") String firstName, 
      @JsonProperty("LAST_NAME") String lastName, 
      @JsonProperty("COACH_SCHOOL_ID") String coachSchoolId, 
      @JsonProperty("DOB") Date dob, 
      @JsonProperty("STUDENT_ID") String studentId,
      @JsonProperty("PARENTS_ED") String parentsEd, 
      @JsonProperty("DOMICILE") String domicile, 
      @JsonProperty("TERMTIME_ACCOM") Integer termtimeAccom, 
      @JsonProperty("SOCIO_EC") Integer socioEc, 
      @JsonProperty("POSTCODE") String postCode, 
      @JsonProperty("HOME_PHONE") String homePhone, 
      @JsonProperty("GENDER") Integer gender, 
      @JsonProperty("OVERSEAS") Integer overseas,
      @JsonProperty("PHOTO_URL") String photoUrl, 
      @JsonProperty("ADDRESS_LINE_1") String addressLine1, 
      @JsonProperty("ADDRESS_LINE_2") String addressLine2, 
      @JsonProperty("ADDRESS_LINE_3") String addressLine3, 
      @JsonProperty("age") Integer age,
      @JsonProperty("createdAt") Date createdAt, 
      @JsonProperty("updatedAt") Date updatedAt,
      @JsonProperty("INSTITUTION_ID") String institutionId) {
    super();
    this.id = id;
    this.mobilePhone = mobilePhone;
    this.firstName = firstName;
    this.lastName = lastName;
    this.coachSchoolId = coachSchoolId;
    this.dob = dob;
    this.studentId = studentId;
    this.parentsEd = parentsEd;
    this.domicile = domicile;
    this.termtimeAccom = termtimeAccom;
    this.socioEc = socioEc;
    this.postCode = postCode;
    this.homePhone = homePhone;
    this.gender = gender;
    this.overseas = overseas;
    this.photoUrl = photoUrl;
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.age = age;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.institutionId = institutionId;
  }

  private String id;
  private String mobilePhone;
  private String firstName;
  private String lastName;
  private String coachSchoolId;
  private Date dob;
  private String studentId;
  private String parentsEd;
  private String domicile;
  private Integer termtimeAccom;
  private Integer socioEc;
  private String postCode;
  private String homePhone;
  private Integer gender;
  private Integer overseas;
  private String photoUrl;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private Integer age;

  public String getId() {
    return id;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getCoachSchoolId() {
    return coachSchoolId;
  }

  public Date getDob() {
    return dob;
  }

  public String getStudentId() {
    return studentId;
  }

  public String getParentsEd() {
    return parentsEd;
  }

  public String getDomicile() {
    return domicile;
  }

  public Integer getTermtimeAccom() {
    return termtimeAccom;
  }

  public Integer getSocioEc() {
    return socioEc;
  }

  public String getPostCode() {
    return postCode;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public Integer getGender() {
    return gender;
  }

  public Integer getOverseas() {
    return overseas;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public Integer getAge() {
    return age;
  }

  @Override
  public String toString() {
    return "JiscStudent [id=" + id + ", mobilePhone=" + mobilePhone + ", firstName=" + firstName + ", lastName=" + lastName + ", coachSchoolId="
        + coachSchoolId + ", dob=" + dob + ", studentId=" + studentId + ", parentsEd=" + parentsEd + ", domicile=" + domicile + ", termtimeAccom="
        + termtimeAccom + ", socioEc=" + socioEc + ", postCode=" + postCode + ", homePhone=" + homePhone + ", gender=" + gender + ", overseas="
        + overseas + ", photoUrl=" + photoUrl + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", addressLine3="
        + addressLine3 + ", age=" + age + "]";
  }

}
