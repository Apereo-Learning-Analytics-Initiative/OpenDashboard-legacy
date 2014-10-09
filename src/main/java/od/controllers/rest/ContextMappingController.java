/**
 * 
 */
package od.controllers.rest;

import java.util.Date;

import od.model.ContextMapping;
import od.model.repository.ContextMappingRepository;

import org.apache.log4j.Logger;
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
public class ContextMappingController {
	
	private Logger log = Logger.getLogger(ContextMappingController.class);
	
	@Autowired private ContextMappingRepository contextMappingRepository;
	
	@RequestMapping(value = "/api/consumer/{consumerKey}/context", method = RequestMethod.POST, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public ContextMapping create(@RequestBody ContextMapping contextMapping) {
		
		if (log.isDebugEnabled()) {
			log.debug(contextMapping);
		}
		
		contextMapping.setModified(new Date());
		return contextMappingRepository.save(contextMapping);
	}

	@RequestMapping(value = "/api/consumer/{consumerKey}/context/{context}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public ContextMapping get(@PathVariable("consumerKey") final String consumerKey,
								@PathVariable("context") final String context) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("get ContextMapping for %s and %s", consumerKey,context));
		}
		
		return contextMappingRepository.findByKeyAndContext(consumerKey, context);
	}
}
