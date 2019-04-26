package od.framework.model;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.oneroster.Enrollment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
@JsonDeserialize(builder = MuseStudent.Builder.class)
public class MuseStudent {
	
private Enrollment enrollment;
private Collection<? extends GrantedAuthority> authentication;

private MuseStudent() {}

  public Enrollment getEnrollment() {
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
  
public static class Builder {
	private MuseStudent _MuseStudent = new MuseStudent();

	public Builder withEnrollment(Enrollment myEnrollment) {
	  _MuseStudent.enrollment = myEnrollment;
	  return this;
	  }
	
	public Builder withAuthentication(Collection<? extends GrantedAuthority> myAuthentication) {
	 _MuseStudent.authentication = myAuthentication;
	 return this;
	 }
	public MuseStudent build() {
	  return _MuseStudent;
	}
  }
 }

	    