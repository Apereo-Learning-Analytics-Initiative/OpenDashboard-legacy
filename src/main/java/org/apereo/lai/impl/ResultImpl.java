/**
 * 
 */
package org.apereo.lai.impl;

import org.apereo.lai.Result;

import od.framework.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class ResultImpl extends OpenDashboardModel implements Result {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String grade;
	private String comments;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
  public String getGrade() {
    return grade;
  }
  public void setGrade(String grade) {
    this.grade = grade;
  }
}
