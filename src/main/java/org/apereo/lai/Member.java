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
				+ ", role=" + role + ", roles=" + roles + ", person=" + person
				+ "]";
	}


}
