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
