/**
 * 
 */
package org.apereo.openlrs.model.event.v2;

import java.io.Serializable;
import java.util.Map;


/**
 * @author ggilbert
 *
 */
public class EventStats implements Serializable {
	private static final long serialVersionUID = 1L;
	private long total;
	private long max;
	private Map<String,Long> totalByWeekNumber;
	private Map<String, StudentEventStats> studentEventStats;

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

	public Map<String, StudentEventStats> getStudentActivityStats() {
		return studentEventStats;
	}

	public void setStudentActivityStats(Map<String, StudentEventStats> studentEventStats) {
		this.studentEventStats = studentEventStats;
	}

	public void setMax(Long maxValue) {
		this.max = maxValue;		
	} 
	
	public Long getMax() {
		return this.max;		
	} 
}
