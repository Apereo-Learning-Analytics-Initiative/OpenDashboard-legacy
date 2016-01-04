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
