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

import od.framework.model.OpenDashboardModel;

import org.apereo.lai.Person;

/**
 * @author ggilbert
 *
 */
public class PersonImpl extends OpenDashboardModel implements Person {

	private static final long serialVersionUID = 1L;
	
	private String contact_email_primary = null;
	private String name_given = null;
	private String name_family = null;
	private String name_full = null;
	private String photo_url = null;
	
	public String getContact_email_primary() {
		return contact_email_primary;
	}
	public void setContact_email_primary(String contact_email_primary) {
		this.contact_email_primary = contact_email_primary;
	}
	public String getName_given() {
		return name_given;
	}
	public void setName_given(String name_given) {
		this.name_given = name_given;
	}
	public String getName_family() {
		return name_family;
	}
	public void setName_family(String name_family) {
		this.name_family = name_family;
	}
	public String getName_full() {
		return name_full;
	}
	public void setName_full(String name_full) {
		this.name_full = name_full;
	}
	
	public String getPhoto_url() {
    return photo_url;
  }
  public void setPhoto_url(String photo_url) {
    this.photo_url = photo_url;
  }
  @Override
  public String toString() {
    return "PersonImpl [contact_email_primary=" + contact_email_primary + ", name_given=" + name_given + ", name_family=" + name_family
        + ", name_full=" + name_full + ", photo_url=" + photo_url + "]";
  }
}
