/**
 * 
 */
package od.controllers.rest;

import java.util.List;

import od.model.CardInstance;
import od.model.repository.CardInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class CardInstanceController {
	
	@Autowired private CardInstanceRepository cardInstanceRepository;
	
	@RequestMapping(value = "/api/cards/{context}/{cardInstanceId}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public CardInstance getOne(@PathVariable("context") final String context, @PathVariable("cardInstanceId") final String cardInstanceId) {
		return cardInstanceRepository.findOne(cardInstanceId);
	}
	
	@RequestMapping(value = "/api/cards/{context}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public List<CardInstance> getAvailableCardsForContext(@PathVariable("context") final String context) {
		return cardInstanceRepository.findByContextOrderBySequenceAsc(context);
	}
	
	@RequestMapping(value = "/api/cards/{context}", method = RequestMethod.POST, 
			produces = "application/json;charset=utf-8")
	public CardInstance create(@RequestBody CardInstance cardInstance) {
		return cardInstanceRepository.save(cardInstance);
	}
	
	@RequestMapping(value = "/api/cards/{context}/{cardInstanceId}", method = RequestMethod.PUT)
	public void update(@PathVariable("context") final String context, 
						@PathVariable("cardInstanceId") final String cardInstanceId,
						@RequestBody CardInstance cardInstance) {
		cardInstanceRepository.save(cardInstance);
	}
	
	@RequestMapping(value = "/api/cards/{context}/{cardInstanceId}", method = RequestMethod.DELETE)
	public void remove(@PathVariable("context") final String context, @PathVariable("cardInstanceId") final String cardInstanceId) {
		cardInstanceRepository.delete(cardInstanceId);
	}
}
