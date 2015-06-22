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
import od.model.assignments.Assignment;
import od.model.roster.Member;
import od.model.roster.Person;
import od.providers.assignments.AssignmentsProvider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class BasicLISAssignmentsProvider implements AssignmentsProvider {

	private static final Logger log = LoggerFactory.getLogger(BasicLISAssignmentsProvider.class);
	
	public static final String USER_ID = "user_id";
	public static final String USER_IMAGE = "user_image";
    public static final String PERSON_SOURCEDID = "person_sourcedid";
    public static final String PERSON_NAME_GIVEN = "person_name_given";
    public static final String PERSON_NAME_FULL = "person_name_full";
    public static final String PERSON_NAME_FAMILY = "person_name_family";
    public static final String LIS_RESULT_SOURCEDID = "lis_result_sourcedid";
    public static final String ROLE = "role";
    public static final String ROLES = "roles";
    public static final String PERSON_CONTACT_EMAIL_PRIMARY = "person_contact_email_primary";
    // tag names for the xml data
    static final String[] rosterDetailInfo= {PERSON_CONTACT_EMAIL_PRIMARY,ROLE,ROLES,LIS_RESULT_SOURCEDID,PERSON_NAME_FAMILY,
            PERSON_NAME_FULL,PERSON_NAME_GIVEN,PERSON_SOURCEDID,USER_ID,USER_IMAGE};
	
	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public Set<Assignment> getAssignments(String url) {
		
		Set<Assignment> assignmentsSet = new HashSet<Assignment>();

        String messageResponse = restTemplate.getForObject(url, String.class);
        
        log.debug("messageResponse {}", messageResponse);
        
        if (StringUtils.isNotBlank(messageResponse)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			Document document = builder.parse(new ByteArrayInputStream(messageResponse.getBytes("UTF-8")));
    			
    			NodeList entities = document.getElementsByTagName("assignment_collection");
    			
    			if (entities != null) {
    				assignmentsSet = new HashSet<Assignment>();
        			for(int i=0;i < entities.getLength();i++) {
                        Map<String, String> nestedMap = new HashMap<String, String>();
                        Node nNode = entities.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        	Element eElement = (Element) nNode;
                            for (String tagName : rosterDetailInfo) {
                                // skip any element in list that doesn't have a corresponding entry in the xml.
                                String text = "";
                                if (eElement.getElementsByTagName(tagName).item(0) != null) {
        							text = eElement.getElementsByTagName(tagName)
        									.item(0).getTextContent();
                                    nestedMap.put(tagName, text);
                                }
                            }
                        }
                        
                        Assignment assignment = new Assignment();
                        assignmentsSet.add(assignment);
        			}
    			}
    		} 
            catch (Exception e) {
            	log.error(e.getMessage(), e);
            	throw new ProviderException(e);
    		}         
        }
        
		
		return assignmentsSet;
	}

}
