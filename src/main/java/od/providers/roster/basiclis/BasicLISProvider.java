/**
 * 
 */
package od.providers.roster.basiclis;

import java.io.ByteArrayInputStream;
import java.security.ProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lti.oauth.OAuthMessageSigner;
import lti.oauth.OAuthUtil;
import od.model.roster.Member;
import od.model.roster.Person;
import od.providers.roster.RosterProvider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author ggilbert
 *
 */
@Component
public class BasicLISProvider{
	
	protected RestTemplate restTemplate = new RestTemplate();

	@Value("${sakai.host}")
	protected String sakaiHost;
    @Value("${sakai.username}")
    protected String sakaiUsername;
    @Value("${sakai.password}")
    protected String sakaiPassword;
	
	protected String getSakaiSession() {
		
		MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String,String>();
		parameters.add("_username", sakaiUsername);
		parameters.add("_password", sakaiPassword);
		
		HttpEntity<MultiValueMap<String,String>> entity =
	            new HttpEntity<MultiValueMap<String, String>>(parameters, null);
		
		return restTemplate.postForObject(sakaiHost + "/direct/session", entity, String.class);
	}
	
}
