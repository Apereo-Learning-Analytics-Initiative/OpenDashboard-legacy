/**
 * 
 */
package od.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author ggilbert
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Dashboard implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private LinkedList<Card> cards;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
