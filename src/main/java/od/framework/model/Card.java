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
package od.framework.model;

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
