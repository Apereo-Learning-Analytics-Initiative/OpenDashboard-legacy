/**
 * 
 */
package od.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContextMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id private String id;
	private String key;
	private String context;
	private Date modified;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	@Override
	public String toString() {
		return "ContextMapping [id=" + id + ", key=" + key + ", context="
				+ context + ", modified=" + modified + "]";
	}
	
	

}
