/**
 * 
 */
package org.apereo.lai.impl;

import java.util.List;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.LineItem;

/**
 * @author ggilbert
 *
 */
public class LineItemImpl extends OpenDashboardModel implements LineItem {

	private static final long serialVersionUID = 1L;
	
	private String type;
	private String title;
	private String context;
	private Double maximumScore;	
	private List<ResultImpl> results;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Double getMaximumScore() {
		return maximumScore;
	}
	public void setMaximumScore(Double maximumScore) {
		this.maximumScore = maximumScore;
	}
	public List<ResultImpl> getResults() {
		return results;
	}
	public void setResults(List<ResultImpl> results) {
		this.results = results;
	}

}
