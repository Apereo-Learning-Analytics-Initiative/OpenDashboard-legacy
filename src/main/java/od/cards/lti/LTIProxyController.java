/**
 * 
 */
package od.cards.lti;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;

import lti.LaunchRequest;
import lti.oauth.OAuthMessageSigner;
import lti.oauth.OAuthUtil;
import od.model.Card;
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
public class LTIProxyController {
	private Logger log = Logger.getLogger(LTIProxyController.class);
	@Autowired private ContextMappingRepository contextMappingRepository;

	@RequestMapping(value = "/api/{contextMappingId}/lti/launch/{cardId}", method = RequestMethod.POST, 
			produces = "application/json;charset=utf-8")
	public ProxiedLaunch proxy(@RequestBody LaunchRequest launchRequest, @PathVariable("contextMappingId") String contextMappingId, @PathVariable("cardId") String cardId) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("contextMappingId: "+contextMappingId);
			log.debug("cardId: "+cardId);
		}
		
		ContextMapping contextMapping = contextMappingRepository.findOne(contextMappingId);
		Card card = contextMapping.findCard(cardId);
		
		if (log.isDebugEnabled()) {
			log.debug(contextMapping);
			log.debug(card);
		}
		
		Map<String, Object> config = card.getConfig();
		
		LaunchRequest proxiedLaunchRequest = new LaunchRequest(
				launchRequest.getLti_message_type(),
				launchRequest.getLti_version(),
				launchRequest.getResource_link_id(),
				launchRequest.getContext_id(),
				null, //Launch_presentation_document_target
				null, //Launch_presentation_width
				null, //Launch_presentation_height
				null, //Launch_presentation_return_url
				launchRequest.getUser_id(),
				launchRequest.getRoles(), 
				launchRequest.getContext_type(),
				launchRequest.getLaunch_presentation_locale(),
				null, //Launch_presentation_css_url
				launchRequest.getRole_scope_mentor(),
				launchRequest.getUser_image(),
				null, // custom
				null, // ext
				launchRequest.getResource_link_title(), 
				null, // resource_link_description 
				launchRequest.getLis_person_name_given(),
				launchRequest.getLis_person_name_family(),
				launchRequest.getLis_person_name_full(),
				launchRequest.getLis_person_contact_email_primary(),

				// TODO make passing outcomes related configurable
				null, //lis_outcome_service_url
				null, //lis_result_sourcedid

				launchRequest.getContext_title(),
				launchRequest.getContext_label(),
				null, //Tool_consumer_info_product_family_code
				null, //Tool_consumer_info_version
				"OpenDashboard", //Tool_consumer_instance_guid
				null, //Tool_consumer_instance_name
				null, //Tool_consumer_instance_description
				null, //Tool_consumer_instance_url
				null, //Tool_consumer_instance_contact_email
				(String)config.get("key"), // oauth_consumer_key
				launchRequest.getOauth_signature_method(),
				launchRequest.getOauth_timestamp(),
				launchRequest.getOauth_nonce(),
				launchRequest.getOauth_version(),
				null, // null oauth_signature is intentional; we calculate it later
				launchRequest.getOauth_callback());
		SortedMap<String,String> sortedParams = proxiedLaunchRequest.toSortedMap();
		
		OAuthMessageSigner oAuthMessageSigner = new OAuthMessageSigner();
		String oauth_signature = oAuthMessageSigner.sign((String)config.get("secret"), OAuthUtil.mapToJava(sortedParams.get(OAuthUtil.SIGNATURE_METHOD_PARAM)), "POST", (String)config.get("launchUrl"), sortedParams);
		sortedParams.put("oauth_signature", oauth_signature);
		
		return new ProxiedLaunch(sortedParams, (String)config.get("launchUrl"));
	}
	
	class ProxiedLaunch implements Serializable {
		private static final long serialVersionUID = 1L;
		private Map<String,String> params;
		private String launchUrl;

		public Map<String, String> getParams() {
			return params;
		}
		public String getLaunchUrl() {
			return launchUrl;
		}
		public ProxiedLaunch(Map<String, String> params, String launchUrl) {
			super();
			this.params = params;
			this.launchUrl = launchUrl;
		}
		
	}
}
