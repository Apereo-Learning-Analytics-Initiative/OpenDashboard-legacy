/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package od.providers.learninglocker;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Member;
import org.apereo.lai.impl.PersonImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Student implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @JsonProperty("_id")
  private String id;
  
  @JsonProperty("FIRST_NAME")
  private String firstName;
  
  @JsonProperty("LAST_NAME")
  private String lastName;
  
  @JsonProperty("STUDENT_ID")
  private String studentId;
  
  @JsonProperty("PHOTO_URL")
  private String photoUrl;
  
  public Member toMember(String role) {
    Member member = new Member();
    member.setId(this.id);
    member.setUser_id(this.studentId);
    member.setRole(role);
    
    PersonImpl person = new PersonImpl();
    StringBuilder fullName = new StringBuilder();
    
    if (StringUtils.isNotBlank(this.firstName)) {
      fullName.append(this.firstName).append(" ");
      person.setName_given(this.firstName);
    }
    
    if (StringUtils.isNotBlank(this.lastName)) {
      fullName.append(this.lastName);
      person.setName_family(this.lastName);
    }
    
    if (fullName.length() > 0) {
      person.setName_full(fullName.toString());
    }
    
    person.setPhoto_url(this.photoUrl);
    
    member.setPerson(person);
    
    return member;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  @Override
  public String toString() {
    return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", studentId=" + studentId + ", photoUrl=" + photoUrl + "]";
  }
  

}
