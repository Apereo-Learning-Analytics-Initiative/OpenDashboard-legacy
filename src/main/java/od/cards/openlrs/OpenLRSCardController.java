/**
 * 
 */
package od.cards.openlrs;

import java.util.Arrays;
import java.util.Map;

import od.cards.InvalidCardConfigurationException;
import od.model.CardInstance;
import od.model.ContextMapping;
import od.model.repository.CardInstanceRepository;
import od.model.repository.ContextMappingRepository;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@Value("${openlrs.url:#{null}}")
	private String openlrsUrl;
	
	@Value("${openlrs.key:#{null}}")
	private String openlrsKey;
	
	@Value("${openlrs.secret:#{null}}")
	private String openlrsSecret;

	@RequestMapping(value = "/api/openlrs/configured", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public boolean configured() {
		boolean configured = true;
		if (StringUtils.isBlank(openlrsKey) || StringUtils.isBlank(openlrsSecret) || StringUtils.isBlank(openlrsUrl)) {
			configured = false;
		}
		
		return configured;
	}
	
	@RequestMapping(value = "/api/{cm}/openlrs/statements", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String getOpenLRSStatements(@PathVariable("cm") String contextMappingId, 
			@RequestParam(value="user",required=false) String user, @RequestParam(value="cardInstanceId",required=false) String cardInstanceId) throws InvalidCardConfigurationException {
		
		if (log.isDebugEnabled()) {
			log.debug("context mapping "+contextMappingId);
			log.debug("cardInstanceId "+cardInstanceId);
			log.debug("user "+user);
		}
		
		if (StringUtils.isBlank(cardInstanceId) && !configured()) {
			//TODO
			throw new InvalidCardConfigurationException();
		}
		
		String baseUrl = null;
		String basicAuth = null;
		
		ContextMapping contextMapping = contextMappingRepository.findOne(contextMappingId);
		String context = contextMapping.getContext();
		
		if (StringUtils.isNotBlank(cardInstanceId)) {
			CardInstance cardInstance = cardInstanceRepository.findOne(cardInstanceId);
			Map<String, Object> config = cardInstance.getConfig();
			baseUrl = (String)config.get("url");
			basicAuth = (String)config.get("key")+":"+(String)config.get("secret");
		}
		else {
			baseUrl = openlrsUrl;
			basicAuth = openlrsKey+":"+openlrsSecret;
		}
		
		return exchange(baseUrl, context, user, basicAuth);
	}
	
	private String exchange(String baseUrl, String context, String user, String basicAuth) {
		
		String expandedUrl = null;
		
		if (user != null) {
			expandedUrl = baseUrl + "/api/user/"+user+"/context/"+ context;
		}
		else {
			expandedUrl = baseUrl + "/api/context/"+ context;
		}
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("expandedUrl: %s",expandedUrl));
		}
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Basic "+new String(Base64.encodeBase64(basicAuth.getBytes())));
		headers.set("X-Experience-API-Version", "1.0.1");
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		return restTemplate.exchange(expandedUrl, HttpMethod.GET, entity, String.class).getBody();
	}

}
