/**
 * 
 */
package od.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CardInstance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id private String id;
	private String cardId;
	private String context;
	private String name;
	private String description;
	private String imgUrl;
	private String cardType;
	private Map<String, Object> config;
	private Integer sequence;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public Map<String, Object> getConfig() {
		return config;
	}
	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	@Override
	public String toString() {
		return "CardInstance [id=" + id + ", cardId=" + cardId + ", context="
				+ context + ", name=" + name + ", description=" + description
				+ ", imgUrl=" + imgUrl + ", cardType=" + cardType + ", config="
				+ config + ", sequence=" + sequence + "]";
	}

}
