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

import javax.servlet.http.HttpServletResponse;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
/*
{"entityPrefix": "assignment", "assignment_collection": [
{
  "access": {
  },
  "attachments": [],
  "authorLastModified": "datest",
  "authors": [],
  "closeTime": {
    "display": "Jun 26, 2015 5:00 pm",
    "time": 1435352400000
  },
  "closeTimeString": "Jun 26, 2015 5:00 pm",
  "content": null,
  "contentReference": "\/assignment\/c\/DAC-EDUCATION-DEPT1-SUBJ1-101\/3d6e5ddb-99f5-4d0e-bcea-e2b7e46b13de",
  "context": "DAC-EDUCATION-DEPT1-SUBJ1-101",
  "creator": "datest",
  "dropDeadTime": {
    "display": "Jun 26, 2015 5:00 pm",
    "time": 1435352400000
  },
  "dropDeadTimeString": "Jun 26, 2015 5:00 pm",
  "dueTime": {
    "display": "Jun 26, 2015 5:00 pm",
    "time": 1435352400000
  },
  "dueTimeString": "Jun 26, 2015 5:00 pm",
  "groups": [],
  "id": "f6377530-afd9-42dd-9335-219a554c5f99",
  "instructions": "<p>test<\/p>",
  "openTime": {
    "display": "Jun 19, 2015 12:00 pm",
    "time": 1434729600000
  },
  "openTimeString": "Jun 19, 2015 12:00 pm",
  "position_order": 0,
  "section": "",
  "status": "Open",
  "timeCreated": {
    "display": "Jun 19, 2015 1:18 pm",
    "time": 1434734282052
  },
  "timeLastModified": {
    "display": "Jun 19, 2015 1:18 pm",
    "time": 1434734282107
  },
  "title": "Test Assignment",
  "draft": false,
  "entityReference": "\/assignment\/f6377530-afd9-42dd-9335-219a554c5f99",
  "entityURL": "http:\/\/localhost:8080\/direct\/assignment\/f6377530-afd9-42dd-9335-219a554c5f99",
  "entityId": "f6377530-afd9-42dd-9335-219a554c5f99",
  "entityTitle": "Test Assignment"
}
]}
*/
@Component
public class BasicLISAssignmentsProvider implements AssignmentsProvider {

	private static final Logger log = LoggerFactory.getLogger(BasicLISAssignmentsProvider.class);
	
	public static final String TITLE = "title";
	public static final String STATUS = "status";
	public static final String ID = "id";
	public static final String INSTRUCTIONS = "instructions";

    // tag names for the xml data
    static final String[] assignmentDetailInfo= {TITLE,STATUS,ID};
	
	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public Set<Assignment> getAssignments(String url, String sessionId) {
		
		Set<Assignment> assignmentsSet = new HashSet<Assignment>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", "JSESSIONID=" + sessionId);
		HttpEntity requestEntity = new HttpEntity(null, headers);
		
		ResponseEntity<String>  messageResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        
        log.debug("messageResponse {}", messageResponse.getBody());
        
        if (StringUtils.isNotBlank(messageResponse.getBody())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			Document document = builder.parse(new ByteArrayInputStream(messageResponse.getBody().getBytes()));
    			
    			NodeList entities = document.getElementsByTagName("assignment_collection");
    			
    			if (entities != null) {
    				assignmentsSet = new HashSet<Assignment>();
        			for(int i=0;i < entities.getLength();i++) {
                        Map<String, String> nestedMap = new HashMap<String, String>();
                        Node nNode = entities.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        	Element eElement = (Element) nNode;
                            for (String tagName : assignmentDetailInfo) {
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
                        assignment.setId(nestedMap.get(ID));
                        assignment.setInstructions(nestedMap.get(INSTRUCTIONS));
                        assignment.setStatus(nestedMap.get(STATUS));
                        assignment.setTitle(nestedMap.get(TITLE));
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
