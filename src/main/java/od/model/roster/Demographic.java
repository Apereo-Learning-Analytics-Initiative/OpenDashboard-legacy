/**
 * 
 */
package od.model.roster;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Demographic extends OpenDashboardModel {

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
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getPercentile() {
		return percentile;
	}
	public void setPercentile(int percentile) {
		this.percentile = percentile;
	}
	public int getSat_verbal() {
		return sat_verbal;
	}
	public void setSat_verbal(int sat_verbal) {
		this.sat_verbal = sat_verbal;
	}
	public int getSat_math() {
		return sat_math;
	}
	public void setSat_math(int sat_math) {
		this.sat_math = sat_math;
	}
	public int getAct_composite() {
		return act_composite;
	}
	public void setAct_composite(int act_composite) {
		this.act_composite = act_composite;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEnrollment_status() {
		return enrollment_status;
	}
	public void setEnrollment_status(String enrollment_status) {
		this.enrollment_status = enrollment_status;
	}
	public int getEarned_credit_hours() {
		return earned_credit_hours;
	}
	public void setEarned_credit_hours(int earned_credit_hours) {
		this.earned_credit_hours = earned_credit_hours;
	}
	public double getGpa_cumulative() {
		return gpa_cumulative;
	}
	public void setGpa_cumulative(double gpa_cumulative) {
		this.gpa_cumulative = gpa_cumulative;
	}
	public double getGpa_semester() {
		return gpa_semester;
	}
	public void setGpa_semester(double gpa_semester) {
		this.gpa_semester = gpa_semester;
	}
	public int getStanding() {
		return standing;
	}
	public void setStanding(int standing) {
		this.standing = standing;
	}
	public String getPell_status() {
		return pell_status;
	}
	public void setPell_status(String pell_status) {
		this.pell_status = pell_status;
	}
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
