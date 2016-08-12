package org.apereo.openlrs.model.event.v2;

import java.io.Serializable;
import java.util.Map;

public class StudentEventStats implements Serializable{
	private static final long serialVersionUID = 1L;
	private long total;
	private Map<String,Long> totalByWeekNumber;
	private String studentId;
	private String studentName;
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public Map<String, Long> getTotalByWeekNumber() {
		return totalByWeekNumber;
	}
	public void setTotalByWeekNumber(Map<String, Long> totalByWeekNumber) {
		this.totalByWeekNumber = totalByWeekNumber;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}	
}
