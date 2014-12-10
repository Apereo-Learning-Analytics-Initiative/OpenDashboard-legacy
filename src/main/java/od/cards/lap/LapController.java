package od.cards.lap;

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
import org.springframework.web.client.RestTemplate;

public class LapController {
	private Logger log = Logger.getLogger(LapController.class);
	@Autowired private CardInstanceRepository cardInstanceRepository;
	@Autowired private ContextMappingRepository contextMappingRepository;
	
	@Value("${lap.url}")
	private String lapUrl;
	
	@Value("${lap.key}")
	private String lapKey;
	
	@Value("${lap.secret}")
	private String lapSecret;
	
	@RequestMapping(value = "/api/lap/{cardInstanceId}/risk", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public String getRiskForCard(@PathVariable("cardInstanceId") String cardInstanceId, @RequestParam(value="user",required=false) String user) {
		
		if (log.isDebugEnabled()) {
			log.debug("cardInstanceId "+cardInstanceId);
			log.debug("user "+user);
		}
		
		CardInstance cardInstance = cardInstanceRepository.findOne(cardInstanceId);
		ContextMapping contextMapping = contextMappingRepository.findOne(cardInstance.getContext());
		Map<String, Object> config = cardInstance.getConfig();
		String url = (String)config.get("url");
		String expandedUrl = url + "/api/riskconfidence?course="+ contextMapping.getContext();
		if (user != null) {
			expandedUrl = expandedUrl + "&user=" + user;
		}
		
		log.debug("expandedUrl: "+expandedUrl);
		String basic = (String)config.get("key")+":"+(String)config.get("secret");
		final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Basic "+new String(encodedBytes));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		return restTemplate.exchange(expandedUrl, HttpMethod.GET, entity, String.class).getBody();
	}

	@RequestMapping(value = "/api/lap/risk", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String getRisk(@PathVariable("context") String context, @RequestParam(value="user",required=false) String user) throws InvalidCardConfigurationException {
		
		if (log.isDebugEnabled()) {
			log.debug("context "+context);
			log.debug("user "+user);
		}
		
		if (StringUtils.isBlank(lapUrl) || StringUtils.isBlank(lapKey) || StringUtils.isBlank(lapSecret)) {
			//TODO
			throw new InvalidCardConfigurationException();
		}
		
		String expandedUrl = lapUrl + "/api/riskconfidence?course="+ context;
		if (user != null) {
			expandedUrl = expandedUrl + "&user=" + user;
		}
		
		log.debug("expandedUrl: "+expandedUrl);
		String basic = lapKey+":"+lapSecret;
		final byte[] encodedBytes = Base64.encodeBase64(basic.getBytes());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Basic "+new String(encodedBytes));
		
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		return restTemplate.exchange(expandedUrl, HttpMethod.GET, entity, String.class).getBody();
	}

}
