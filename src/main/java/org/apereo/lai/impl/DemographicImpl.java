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

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Demographic;

/**
 * @author ggilbert
 *
 */
public class DemographicImpl extends OpenDashboardModel implements Demographic {

	private static final long serialVersionUID = 1L;
	
	private String user_id;
	private int percentile;
	private int sat_verbal;
	private int sat_math;
	private int act_composite;
	private int age;
	private String race;
	private String gender;
	private String enrollment_status;
	private int earned_credit_hours;
	private double gpa_cumulative;
	private double gpa_semester;
	private int standing;
	private String pell_status;
	private String class_code;
	
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getUser_id()
   */
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getPercentile()
   */
	public int getPercentile() {
		return percentile;
	}
	public void setPercentile(int percentile) {
		this.percentile = percentile;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getSat_verbal()
   */
	public int getSat_verbal() {
		return sat_verbal;
	}
	public void setSat_verbal(int sat_verbal) {
		this.sat_verbal = sat_verbal;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getSat_math()
   */
	public int getSat_math() {
		return sat_math;
	}
	public void setSat_math(int sat_math) {
		this.sat_math = sat_math;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getAct_composite()
   */
	public int getAct_composite() {
		return act_composite;
	}
	public void setAct_composite(int act_composite) {
		this.act_composite = act_composite;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getAge()
   */
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getRace()
   */
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getGender()
   */
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getEnrollment_status()
   */
	public String getEnrollment_status() {
		return enrollment_status;
	}
	public void setEnrollment_status(String enrollment_status) {
		this.enrollment_status = enrollment_status;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getEarned_credit_hours()
   */
	public int getEarned_credit_hours() {
		return earned_credit_hours;
	}
	public void setEarned_credit_hours(int earned_credit_hours) {
		this.earned_credit_hours = earned_credit_hours;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getGpa_cumulative()
   */
	public double getGpa_cumulative() {
		return gpa_cumulative;
	}
	public void setGpa_cumulative(double gpa_cumulative) {
		this.gpa_cumulative = gpa_cumulative;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getGpa_semester()
   */
	public double getGpa_semester() {
		return gpa_semester;
	}
	public void setGpa_semester(double gpa_semester) {
		this.gpa_semester = gpa_semester;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getStanding()
   */
	public int getStanding() {
		return standing;
	}
	public void setStanding(int standing) {
		this.standing = standing;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getPell_status()
   */
	public String getPell_status() {
		return pell_status;
	}
	public void setPell_status(String pell_status) {
		this.pell_status = pell_status;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Demographic#getClass_code()
   */
	public String getClass_code() {
		return class_code;
	}
	public void setClass_code(String class_code) {
		this.class_code = class_code;
	}
	@Override
	public String toString() {
		return "Demographic [user_id=" + user_id + ", percentile=" + percentile
				+ ", sat_verbal=" + sat_verbal + ", sat_math=" + sat_math
				+ ", act_composite=" + act_composite + ", age=" + age
				+ ", race=" + race + ", gender=" + gender
				+ ", enrollment_status=" + enrollment_status
				+ ", earned_credit_hours=" + earned_credit_hours
				+ ", gpa_cumulative=" + gpa_cumulative + ", gpa_semester="
				+ gpa_semester + ", standing=" + standing + ", pell_status="
				+ pell_status + ", class_code=" + class_code + "]";
	}

}
