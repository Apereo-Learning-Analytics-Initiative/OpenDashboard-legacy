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
package od.framework.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.Set;


/**
 * @author ggilbert
 *
 */
public class ContextMapping extends OpenDashboardModel {
    private static final long serialVersionUID = 1L;

    private String context;
    private String tenantId;
    private Set<Dashboard> dashboards;
    private Date modified;

    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getTenantId() {
      return tenantId;
    }
    public void setTenantId(String tenantId) {
      this.tenantId = tenantId;
    }
    public Date getModified() {
        return modified;
    }
    public void setModified(Date modified) {
        this.modified = modified;
    }
    public Set<Dashboard> getDashboards() {
        return dashboards;
    }
    public void setDashboards(Set<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }

    public Card findCard(String cardId) {
        Card card = null;

        if (dashboards != null && !dashboards.isEmpty()) {
            outer:for (Dashboard dashboard : dashboards) {
                LinkedList<Card> cards = dashboard.getCards();
                if (cards != null && !cards.isEmpty()) {
                    for (Card c : cards) {
                        if (c.getId() != null && c.getId().equals(cardId)) {
                            card = c;
                            break outer;
                        }
                    }
                }
            }
        }

        return card;
    }
}
