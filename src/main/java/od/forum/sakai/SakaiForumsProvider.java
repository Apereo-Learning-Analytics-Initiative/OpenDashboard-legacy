/**
 * 
 */
package od.forum.sakai;

import java.io.ByteArrayInputStream;
import java.security.ProviderException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import od.forum.Forum;
import od.forum.ForumsProvider;
import od.providers.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author ggilbert
 *
 */
/*
 * {"entityPrefix": "forums", "forums_collection": [ { "attachments": [],
 * "createdDate": 1435067651, "creator": "admin", "extendedDescription": null,
 * "id": 1, "isDraft": false, "isModerated": false, "modifiedDate": 1435067682,
 * "modifier": "datest", "readMessages": 0, "shortDescription": null, "title":
 * "DAC-EDUCATION-DEPT1-SUBJ1-101 Forum", "totalMessages": 0, "entityReference":
 * "\/forums\/1", "entityURL": "http:\/\/localhost:8080\/direct\/forums\/1",
 * "entityId": "1", "entityTitle": "DAC-EDUCATION-DEPT1-SUBJ1-101 Forum" } ]}
 */
@Component
public class SakaiForumsProvider extends BaseSakaiProvider implements ForumsProvider {

  private static final Logger log = LoggerFactory.getLogger(SakaiForumsProvider.class);

  public static final String TITLE = "forumTitle";
  public static final String ID = "forumId";

  // tag names for the xml data
  static final String[] assignmentDetailInfo = { TITLE, ID };

  @Override
  public Set<Forum> getForums(Map<String, String> options) {

    Set<Forum> forumsSet = new HashSet<Forum>();

    String url = sakaiHost + "/direct/topic/site/" + options.get("courseId") + ".xml";
    ResponseEntity<String> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(url), String.class);

    log.debug("messageResponse {}", messageResponse.getBody());

    if (StringUtils.isNotBlank(messageResponse.getBody())) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(messageResponse.getBody().getBytes()));

        NodeList entities = document.getElementsByTagName("topic_collection");

        if (entities != null) {
          forumsSet = new HashSet<Forum>();
          for (int i = 0; i < entities.getLength(); i++) {
            Map<String, String> nestedMap = new HashMap<String, String>();
            Node nNode = entities.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
              for (String tagName : assignmentDetailInfo) {
                // skip any element in list that doesn't have a corresponding
                // entry in the xml.
                String text = "";
                if (eElement.getElementsByTagName(tagName).item(0) != null) {
                  text = eElement.getElementsByTagName(tagName).item(0).getTextContent();
                  nestedMap.put(tagName, text);
                }
              }
            }

            Forum forum = new Forum();
            forum.setId(nestedMap.get(ID));
            forum.setTitle(nestedMap.get(TITLE));
            forumsSet.add(forum);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new ProviderException(e);
      }
    }

    return forumsSet;
  }

}
