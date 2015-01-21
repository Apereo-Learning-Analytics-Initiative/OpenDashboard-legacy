package od.model;

import java.util.Map;

/**
 * @author ggilbert
 *
 */

public class Card extends OpenDashboardModel {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String imgUrl;
    private String cardType;
    private String styleClasses;
    private Map<String, Object> config;

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
