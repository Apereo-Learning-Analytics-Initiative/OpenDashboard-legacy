package org.apereo.lai.impl;

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Assignment;

public class AssignmentImpl extends OpenDashboardModel implements Assignment {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String status;
	private String instructions;
	private String context;
	
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Assignment#getTitle()
   */
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Assignment#getStatus()
   */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Assignment#getInstructions()
   */
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	/* (non-Javadoc)
   * @see org.apereo.lai.impl.Assignment#getContext()
   */
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
}
