package org.apereo.lai;

public interface Demographic {

  String getUser_id();

  int getPercentile();

  int getSat_verbal();

  int getSat_math();

  int getAct_composite();

  int getAge();

  String getRace();

  String getGender();

  String getEnrollment_status();

  int getEarned_credit_hours();

  double getGpa_cumulative();

  double getGpa_semester();

  int getStanding();

  String getPell_status();

  String getClass_code();

}