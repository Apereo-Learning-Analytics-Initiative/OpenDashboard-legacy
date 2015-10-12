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
