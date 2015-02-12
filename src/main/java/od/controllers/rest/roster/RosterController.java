/**
 * 
 */
package od.controllers.rest.roster;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import lti.oauth.OAuthMessageSigner;
import lti.oauth.OAuthUtil;
import od.cards.InvalidCardConfigurationException;
import od.cards.openlrs.OpenLRSCardController.RestTemplateFactory;
import od.cards.openlrs.OpenLRSCardController.RestTemplateFactoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author ggilbert
 *
 */
@RestController
public class RosterController {
	
	private static final Logger log = LoggerFactory.getLogger(RosterController.class);
	private RestTemplateFactoryInterface restTemplateFactory = new RestTemplateFactory();
	
    @Value("${auth.oauth.key}")
    private String key;
    @Value("${auth.oauth.secret}")
    private String secret;

	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/{contextMappingId}/db/{dashboardId}/roster/{cardId}/basiclis", method = RequestMethod.POST)
	public String getRosterUsingBasicLIS(
			@PathVariable("contextMappingId") String contextMappingId,
			@PathVariable("dashboardId") String dashboardId,
			@PathVariable("cardId") String cardId,
			@RequestBody Map<String, String> basicLISData)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("contextMappingId " + contextMappingId);
			log.debug("dashboardId " + dashboardId);
			log.debug("cardId " + cardId);
			log.debug("basicLISData " + basicLISData);
		}
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("lti_version", "LTI-1p0");
		map.add("lti_message_type", "basic-lis-readmembershipsforcontext");
		map.add("id", basicLISData.get("ext_ims_lis_memberships_id"));
		map.add(OAuthUtil.CONSUMER_KEY_PARAM, key);
		map.add(OAuthUtil.SIGNATURE_METHOD_PARAM, "HMAC-SHA1");
		map.add(OAuthUtil.VERSION_PARAM, "1.0");
		map.add(OAuthUtil.TIMESTAMP_PARAM, new Long((new Date().getTime()) / 1000).toString());
		map.add(OAuthUtil.NONCE_PARAM, UUID.randomUUID().toString());
        map.add(OAuthUtil.SIGNATURE_PARAM, new OAuthMessageSigner().sign(secret,OAuthUtil.mapToJava("HMAC-SHA1"),"POST", basicLISData.get("ext_ims_lis_memberships_url"), new TreeMap<String, String>(map.toSingleValueMap())));

		return restTemplateFactory.createRestTemplate().postForObject(basicLISData.get("ext_ims_lis_memberships_url"), map, String.class);
	}

	public interface RestTemplateFactoryInterface {
		public RestTemplate createRestTemplate();
	}

	public class RestTemplateFactory implements RestTemplateFactoryInterface {
		@Override
		public RestTemplate createRestTemplate() {
			return new RestTemplate();
		}
	}

	public RestTemplateFactoryInterface getRestTemplateFactory() {
		return restTemplateFactory;
	}

	public void setRestTemplateFactory(
			RestTemplateFactoryInterface restTemplateFactory) {
		this.restTemplateFactory = restTemplateFactory;
	}

}
