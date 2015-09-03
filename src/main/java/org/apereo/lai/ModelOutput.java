package org.apereo.lai;

import java.util.Date;

public interface ModelOutput {

  String getStudentId();
  String getCourseId();
  String getRisk_score();
  Date getCreated_date();

}