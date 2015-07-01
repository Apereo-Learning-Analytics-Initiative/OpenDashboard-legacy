/**
 * 
 */
package od.roster.basiclis;

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
import od.providers.ProviderOptions;
import od.roster.Member;
import od.roster.Person;
import od.roster.RosterProvider;

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
public class BasicLISRosterProvider implements RosterProvider {

  private static final Logger log = LoggerFactory.getLogger(BasicLISRosterProvider.class);

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
  static final String[] rosterDetailInfo = { PERSON_CONTACT_EMAIL_PRIMARY, ROLE, ROLES, LIS_RESULT_SOURCEDID, PERSON_NAME_FAMILY, PERSON_NAME_FULL,
      PERSON_NAME_GIVEN, PERSON_SOURCEDID, USER_ID, USER_IMAGE };

  private RestTemplate restTemplate = new RestTemplate();
  @Value("${auth.oauth.key}")
  private String key;
  @Value("${auth.oauth.secret}")
  private String secret;

  @Override
  public Set<Member> getRoster(ProviderOptions options) {
    String url = options.getStrategyHost();
    String rosterIdentifier = options.getStrategyKey();
    
    log.debug("url: {}", url);
    log.debug("rosterIdentifier: {}", rosterIdentifier);

    Set<Member> memberSet = null;

    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("lti_version", "LTI-1p0");
    map.add("lti_message_type", "basic-lis-readmembershipsforcontext");
    map.add("id", rosterIdentifier);
    map.add(OAuthUtil.CONSUMER_KEY_PARAM, key);
    map.add(OAuthUtil.SIGNATURE_METHOD_PARAM, "HMAC-SHA1");
    map.add(OAuthUtil.VERSION_PARAM, "1.0");
    map.add(OAuthUtil.TIMESTAMP_PARAM, new Long((new Date().getTime()) / 1000).toString());
    map.add(OAuthUtil.NONCE_PARAM, UUID.randomUUID().toString());
    try {
      map.add(OAuthUtil.SIGNATURE_PARAM,
          new OAuthMessageSigner().sign(secret, OAuthUtil.mapToJava("HMAC-SHA1"), "POST", url, new TreeMap<String, String>(map.toSingleValueMap())));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ProviderException(e);
    }

    String messageResponse = restTemplate.postForObject(url, map, String.class);
    log.debug("messageResponse {}", messageResponse);

    if (StringUtils.isNotBlank(messageResponse)) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(messageResponse.getBytes("UTF-8")));

        NodeList members = document.getElementsByTagName("member");

        if (members != null) {
          memberSet = new HashSet<Member>();
          for (int i = 0; i < members.getLength(); i++) {
            Map<String, String> nestedMap = new HashMap<String, String>();
            Node nNode = members.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
              for (String tagName : rosterDetailInfo) {
                // skip any element in list that doesn't have a corresponding
                // entry in the xml.
                String text = "";
                if (eElement.getElementsByTagName(tagName).item(0) != null) {
                  text = eElement.getElementsByTagName(tagName).item(0).getTextContent();
                  nestedMap.put(tagName, text);
                }
              }
            }

            Member member = new Member();
            member.setRole(nestedMap.get(ROLE));
            member.setRoles(nestedMap.get(ROLES));
            member.setUser_id(nestedMap.get(USER_ID));
            member.setUser_image(nestedMap.get(USER_IMAGE));
            Person person = new Person();
            person.setContact_email_primary(nestedMap.get(PERSON_CONTACT_EMAIL_PRIMARY));
            person.setName_family(nestedMap.get(PERSON_NAME_FAMILY));
            person.setName_full(nestedMap.get(PERSON_NAME_FULL));
            person.setName_given(nestedMap.get(PERSON_NAME_GIVEN));
            member.setPerson(person);

            memberSet.add(member);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new ProviderException(e);
      }
    }

    return memberSet;
  }

}
