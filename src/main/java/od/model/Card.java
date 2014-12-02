package od.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * @author ggilbert
 *
 */
public class Card implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id private String id;
	private String name;
	private String description;
	private String imgUrl;
	private String cardType;
	private Map<String, Object> config;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "Card [id=" + id + ", name=" + name + ", description="
				+ description + ", imgUrl=" + imgUrl + ", cardType=" + cardType
				+ ", config=" + config + "]";
	}
}
