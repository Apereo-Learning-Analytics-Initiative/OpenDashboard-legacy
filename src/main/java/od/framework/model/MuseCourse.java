package od.framework.model;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Class;

@JsonDeserialize(builder = MuseCourse.Builder.class)
public class MuseCourse {

	private Set<Enrollment> enrollments = null;
	private Class course = null;
	
	private MuseCourse() {}
	
	public Class getCourse() {
	    return this.course;
	}
	
	public Set<Enrollment> getEnrollments() {
	    return this.enrollments;
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
		private MuseCourse _MuseCourse = new MuseCourse();
				
		public Builder withEnrollments(Set<Enrollment> students) {
			 _MuseCourse.enrollments = students;
			 return this;
		}
		
		public Builder withCourse(Class course) {
			 _MuseCourse.course = course;
			 return this;
		}
		
		public MuseCourse build() {
			return _MuseCourse;
		}
			   
	}
}