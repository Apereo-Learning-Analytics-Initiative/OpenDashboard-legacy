/**
 * 
 */
package od.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * @author ggilbert
 *
 */
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id private String id;
	private Map<String,Object> data;
	private long timestamp;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
