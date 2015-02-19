/**
 * 
 */
package od.model.roster;

import od.model.OpenDashboardModel;

/**
 * @author ggilbert
 *
 */
public class Person extends OpenDashboardModel {

	private static final long serialVersionUID = 1L;
	
	private String contact_email_primary = null;
	private String name_given = null;
	private String name_family = null;
	private String name_full = null;
	
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
	
	@Override
	public String toString() {
		return "Person [contact_email_primary=" + contact_email_primary
				+ ", name_given=" + name_given + ", name_family=" + name_family
				+ ", name_full=" + name_full + "]";
	}
}
