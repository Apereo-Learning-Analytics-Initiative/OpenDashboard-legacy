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
public class Card implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private String description;
	private String imgUrl;
	private String cardType;
	private String styleClasses;
	private Map<String, Object> config;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStyleClasses() {
		return styleClasses;
	}
	public void setStyleClasses(String styleClasses) {
		this.styleClasses = styleClasses;
	}
	@Override
	public String toString() {
		return "Card [id=" + id + ", title=" + title + ", description="
				+ description + ", imgUrl=" + imgUrl + ", cardType=" + cardType
				+ ", styleClasses=" + styleClasses + ", config=" + config + "]";
	}
}
