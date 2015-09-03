/**
 * 
 */
package org.apereo.lai.impl;

import java.util.Date;

import org.apereo.lai.ModelOutput;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
public class ModelOutputImpl implements ModelOutput {
  
  @JsonProperty("student_id")
  private String studentId;
  @JsonProperty("course_id")
  private String courseId;
  private String risk_score;
  @CreatedDate
  private Date created_date;
  private String model_run_id;
  public String getStudentId() {
    return studentId;
  }
  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }
  public String getCourseId() {
    return courseId;
  }
  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }
  public String getRisk_score() {
    return risk_score;
  }
  public void setRisk_score(String risk_score) {
    this.risk_score = risk_score;
  }
  public Date getCreated_date() {
    return created_date;
  }
  public void setCreated_date(Date created_date) {
    this.created_date = created_date;
  }
  public String getModel_run_id() {
    return model_run_id;
  }
  public void setModel_run_id(String model_run_id) {
    this.model_run_id = model_run_id;
  }
  
}
