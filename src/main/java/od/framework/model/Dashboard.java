/**
 *
 */
package od.framework.model;

import java.util.LinkedList;

/**
 * @author ggilbert
 *
 */
public class Dashboard extends OpenDashboardModel {
    private static final long serialVersionUID = 1L;

    private String title;
    private LinkedList<Card> cards;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LinkedList<Card> getCards() {
        return cards;
    }
    public void setCards(LinkedList<Card> cards) {
        this.cards = cards;
    }
}
