/**
 * 
 */
package od.cards.openlrs;

import java.util.Arrays;
import java.util.Map;

import od.model.CardInstance;
import od.model.ContextMapping;
import od.model.repository.CardInstanceRepository;
import od.model.repository.ContextMappingRepository;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
@RestController
public class OpenLRSCardController {
	private Logger log = Logger.getLogger(OpenLRSCardController.class);
	@Autowired private CardInstanceRepository cardInstanceRepository;
	@Autowired private ContextMappingRepository contextMappingRepository;
	
	@RequestMapping(value = "/api/openlrs/{cardInstanceId}/statements", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public String getOpenLRSStatements(@PathVariable("cardInstanceId") String cardInstanceId) {
		CardInstance cardInstance = cardInstanceRepository.findOne(cardInstanceId);
		ContextMapping contextMapping = contextMappingRepository.findOne(cardInstance.getContext());
		Map<String, Object> config = cardInstance.getConfig();
		String url = (String)config.get("url");
		
		String expandedUrl = url + "/api/context/"+ contextMapping.getContext();
		log.debug("expandedUrl: "+expandedUrl);
		String basic = (String)config.get("key")+":"+(String)config.get("secret");
		final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Basic "+new String(encodedBytes));
		headers.set("X-Experience-API-Version", "1.0.1");
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		return restTemplate.exchange(expandedUrl, HttpMethod.GET, entity, String.class).getBody();
	}
}
