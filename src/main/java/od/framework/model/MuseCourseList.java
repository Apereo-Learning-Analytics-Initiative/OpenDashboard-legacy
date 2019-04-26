package od.framework.model;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.oneroster.Enrollment;
import org.springframework.security.core.GrantedAuthority;
@JsonDeserialize(builder = MuseStudent.Builder.class)
public class MuseCourseList {
	
private Set<Enrollment> enrollment;
private Collection<? extends GrantedAuthority> authentication;

private MuseCourseList() {}

	public Set<Enrollment> getEnrollments() {
		return this.enrollment;
  }
  
  public Collection<? extends GrantedAuthority> getAuthentication(){
	  return this.authentication;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public int hashCode() {
  return 31415;
  }

  @Override
  public boolean equals(Object obj) {
  return true;
  }
  
  public boolean allowStudent;
  
  public boolean allowInstructor;

  public boolean allowDefault;
  
public static class Builder {
	private MuseCourseList _MuseCourseList = new MuseCourseList();

	public Builder withEnrollment(Set<Enrollment> myEnrollments) {
	  _MuseCourseList.enrollment = myEnrollments;
	  return this;
	  }
	
	public Builder withAllowStudent(boolean bool){
		_MuseCourseList.allowStudent = bool;
		return this;
	}
	
	public Builder withAllowInstructor(boolean bool){
		_MuseCourseList.allowInstructor = bool;
		return this;
	}
	
	public Builder withAllowDefault(boolean bool){
		_MuseCourseList.allowDefault = bool;
		return this;
	}
	
	public Builder withAuthentication(Collection<? extends GrantedAuthority> myAuthentication) {
	 _MuseCourseList.authentication = myAuthentication;
	 return this;
	 }
	public MuseCourseList build() {
	  return _MuseCourseList;
	}
  }
 }

	    