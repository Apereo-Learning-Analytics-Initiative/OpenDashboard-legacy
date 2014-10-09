/**
 * 
 */
package od.controllers.rest;

import java.util.List;

import od.model.Card;
import od.model.repository.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class CardController {
	
	@Autowired private CardRepository cardRepository;
	
	@RequestMapping(value = "/api/cards", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public List<Card> getAvailableCards() {
		return cardRepository.findAll();
	}
}
