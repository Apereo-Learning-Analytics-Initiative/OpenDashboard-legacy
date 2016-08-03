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
package org.apereo.lai;

import od.framework.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Member extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String user_id = null;
	private String user_image = null;
	private String role = null;
	private String roles = null;
	private Person person = null;
	private String person_sourcedid = null;
	
	public String getPerson_sourcedid() {
		return person_sourcedid;
	}
	public void setPerson_sourcedid(String person_sourcedid) {
		this.person_sourcedid = person_sourcedid;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_image() {
		return user_image;
	}
	public void setUser_image(String user_image) {
		this.user_image = user_image;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	@Override
	public String toString() {
		return "Member [user_id=" + user_id + ", user_image=" + user_image
				+ ", role=" + role + ", roles=" + roles + ", person=" + person + ", person_sourcedid=" + person_sourcedid
				+ "]";
	}


}
