/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.lti;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import ltistarter.config.ApplicationConfig;
import ltistarter.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * LTI Request object holds all the details for a valid LTI request
 * (including data populated on the validated launch)
 *
 * This is generally the only class that a developer will need to interact with but it will
 * only be available during incoming LTI requests (launches, etc.). Once the tool application
 * takes over and is servicing the requests on its own path this will no longer be available.
 *
 * Obtain this class using the static instance methods like so (recommended):
 * LTIRequest ltiRequest = LTIRequest.getInstanceOrDie();
 *
 * Or by retrieving it from the HttpServletRequest attributes like so (best to not do this):
 * LTIRequest ltiRequest = (LTIRequest) req.getAttribute(LTIRequest.class.getName());
 *
 * Devs may also need to use the LTIDataService service (injected) to access data when there is no
 * LTI request active.
 *
 * The main LTI data will also be placed into the Session and the Principal under the
 * LTI_USER_ID, LTI_CONTEXT_ID, and LTI_ROLE_ID constant keys.
 *
 * NOTE: This basically does everything in lti_db.php from tsugi (except the OAuth stuff, that is handled by spring security)
 */
public class LTIRequest {

    final static Logger log = LoggerFactory.getLogger(LTIRequest.class);

    static final String LIS_PERSON_PREFIX = "lis_person_name_";

    public static final String LTI_CONSUMER_KEY = "oauth_consumer_key";
    public static final String LTI_CONTEXT_ID = "context_id";
    public static final String LTI_CONTEXT_TITLE = "context_title";
    public static final String LTI_CONTEXT_LABEL = "context_label";
    public static final String LTI_LINK_ID = "resource_link_id";
    public static final String LTI_LINK_TITLE = "resource_link_title";
    public static final String LTI_LINK_DESC = "resource_link_description";
    public static final String LTI_MESSAGE_TYPE = "lti_message_type";
    public static final String LTI_PRES_LOCALE = "launch_presentation_locale";
    public static final String LTI_PRES_TARGET = "launch_presentation_document_target";
    public static final String LTI_PRES_WIDTH = "launch_presentation_width";
    public static final String LTI_PRES_HEIGHT = "launch_presentation_height";
    public static final String LTI_PRES_RETURN_URL = "launch_presentation_return_url";
    public static final String LTI_SERVICE = "lis_outcome_service_url";
    public static final String LTI_SOURCEDID = "lis_result_sourcedid";
    public static final String LTI_TOOL_CONSUMER_CODE = "tool_consumer_info_product_family_code";
    public static final String LTI_TOOL_CONSUMER_VERSION = "tool_consumer_info_version";
    public static final String LTI_TOOL_CONSUMER_NAME = "tool_consumer_instance_name";
    public static final String LTI_TOOL_CONSUMER_EMAIL = "tool_consumer_instance_contact_email";
    public static final String LTI_TOOL_CONSUMER_PROFILE_URL = "tc_profile_url";
    public static final String LTI_USER_ID = "user_id";
    public static final String LTI_USER_EMAIL = "lis_person_contact_email_primary";
    public static final String LTI_USER_NAME_FULL = LIS_PERSON_PREFIX + "full";
    public static final String LTI_USER_IMAGE_URL = "user_image";
    public static final String LTI_USER_ROLES = "roles";
    public static final String LTI_USER_ROLE = "user_role";
    public static final String LTI_VERSION = "lti_version";
    public static final String USER_ROLE_OVERRIDE = "role_override";

    public static final String LTI_MESSAGE_TYPE_BASIC = "basic-lti-launch-request";
    public static final String LTI_MESSAGE_TYPE_PROXY_REG = "ToolProxyRegistrationRequest";
    public static final String LTI_MESSAGE_TYPE_PROXY_REREG = "ToolProxyReregistrationRequest";
    public static final String LTI_VERSION_1P0 = "LTI-1p0";
    public static final String LTI_VERSION_2P0 = "LTI-2p0";
    public static final String LTI_ROLE_GENERAL = "user";
    public static final String LTI_ROLE_LEARNER = "learner";
    public static final String LTI_ROLE_INSTRUCTOR = "instructor";
    public static final String LTI_ROLE_ADMIN = "administrator";

    HttpServletRequest httpServletRequest;
    LTIDataService ltiDataService;
    RestTemplate restTemplate;

    // these are populated by the loadLTIDataFromDB operation
    LtiKeyEntity key;
    LtiContextEntity context;
    LtiLinkEntity link;
    LtiMembershipEntity membership;
    LtiUserEntity user;
    LtiServiceEntity service;
    LtiResultEntity result;
    //ProfileEntity profile;
    boolean loaded = false;
    boolean complete = false;
    boolean updated = false;
    int loadingUpdates = 0;

    // these are populated on construct
    String ltiContextId;
    String ltiContextTitle;
    String ltiContextLabel;
    String ltiConsumerKey;
    String ltiLinkId;
    String ltiLinkTitle;
    String ltiLinkDescription;
    Locale ltiPresLocale;
    String ltiPresTarget;
    int ltiPresWidth;
    int ltiPresHeight;
    String ltiPresReturnUrl;
    String ltiMessageType;
    String ltiServiceId;
    String ltiSourcedid;
    String ltiToolConsumerCode;
    String ltiToolConsumerVersion;
    String ltiToolConsumerName;
    String ltiToolConsumerEmail;
    String ltiUserId;
    String ltiUserEmail;
    String ltiUserDisplayName;
    String ltiUserImageUrl;
    String rawUserRoles;
    Set<String> ltiUserRoles;
    int userRoleNumber;
    String rawUserRolesOverride;
    String ltiVersion;

    /**
     * @return the current LTIRequest object if there is one available, null if there isn't one and this is not a valid LTI based request
     */
    public static synchronized LTIRequest getInstance() {
        LTIRequest ltiRequest = null;
        try {
            ltiRequest = getInstanceOrDie();
        } catch (Exception e) {
            // nothing to do here
        }
        return ltiRequest;
    }

    /**
     * @return the current LTIRequest object if there is one available
     * @throws java.lang.IllegalStateException if the LTIRequest cannot be obtained
     */
    public static LTIRequest getInstanceOrDie() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        if (req == null) {
            throw new IllegalStateException("No HttpServletRequest can be found, cannot get the LTIRequest unless we are currently in a request");
        }
        LTIRequest ltiRequest = (LTIRequest) req.getAttribute(LTIRequest.class.getName());
        if (ltiRequest == null) {
            log.debug("No LTIRequest found, attempting to create one for the current request");
            LTIDataService ltiDataService = null;
            try {
                ltiDataService = ApplicationConfig.getContext().getBean(LTIDataService.class);
            } catch (Exception e) {
                log.warn("Unable to get the LTIDataService, initializing the LTIRequest without it");
            }
            try {
                if (ltiDataService != null) {
                    ltiRequest = new LTIRequest(req, ltiDataService, true);
                } else {
                    ltiRequest = new LTIRequest(req);
                }
            } catch (Exception e) {
                log.warn("Failure trying to create the LTIRequest: " + e);
            }
        }
        if (ltiRequest == null) {
            throw new IllegalStateException("Invalid LTI request, cannot create LTIRequest from request: " + req);
        }
        return ltiRequest;
    }

    /**
     * @param request an http servlet request
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request) {
        assert request != null : "cannot make an LtiRequest without a request";
        this.httpServletRequest = request;
        this.restTemplate = new RestTemplate();
        // extract the typical LTI data from the request
        if (!isLTIRequest(request)) {
            throw new IllegalStateException("Request is not an LTI request");
        }
        processRequestParameters(request);
    }

    /**
     * @param request an http servlet request
     * @param ltiDataService   the service used for accessing LTI data
     * @param update  if true then update (or insert) the DB records for this request (else skip DB updating)
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request, LTIDataService ltiDataService, boolean update) {
        this(request);
        assert ltiDataService != null : "LTIDataService cannot be null";
        ltiDataService.loadLTIDataFromDB(this);
        if (update) {
            ltiDataService.updateLTIDataInDB(this);
        }
        this.ltiDataService = ltiDataService;
    }

    /**
     * @param paramName the request parameter name
     * @return the value of the parameter OR null if there is none
     */
    public String getParam(String paramName) {
        String value = null;
        if (this.httpServletRequest != null && paramName != null) {
            value = StringUtils.trimToNull(this.httpServletRequest.getParameter(paramName));
        }
        return value;
    }

    /**
     * Processes all the parameters in this request into populated internal variables in the LTI Request
     *
     * @param request an http servlet request
     * @return true if this is a complete LTI request (includes key, context, link, user) OR false otherwise
     */
    public boolean processRequestParameters(HttpServletRequest request) {
        if (request != null && this.httpServletRequest != request) {
            this.httpServletRequest = request;
        }
        assert this.httpServletRequest != null;

        ltiMessageType = getParam(LTI_MESSAGE_TYPE);
        ltiVersion = getParam(LTI_VERSION);
        // These 4 really need to be populated for this LTI request to make any sense...
        ltiConsumerKey = getParam(LTI_CONSUMER_KEY);
        ltiContextId = getParam(LTI_CONTEXT_ID);
        ltiLinkId = getParam(LTI_LINK_ID);
        ltiUserId = getParam(LTI_USER_ID);
        complete = checkCompleteLTIRequest(false);
        // OPTIONAL fields below
        ltiServiceId = getParam(LTI_SERVICE);
        ltiSourcedid = getParam(LTI_SOURCEDID);
        ltiUserEmail = getParam(LTI_USER_EMAIL);
        ltiUserImageUrl = getParam(LTI_USER_IMAGE_URL);
        ltiLinkTitle = getParam(LTI_LINK_TITLE);
        ltiLinkDescription = getParam(LTI_LINK_DESC);
        ltiContextTitle = getParam(LTI_CONTEXT_TITLE);
        ltiContextLabel = getParam(LTI_CONTEXT_LABEL);
        String localeStr = getParam(LTI_PRES_LOCALE);
        if (localeStr == null) {
            ltiPresLocale = Locale.getDefault();
        } else {
            ltiPresLocale = Locale.forLanguageTag(localeStr);
        }
        ltiPresTarget = getParam(LTI_PRES_TARGET);
        ltiPresWidth = NumberUtils.toInt(getParam(LTI_PRES_WIDTH), 0);
        ltiPresHeight = NumberUtils.toInt(getParam(LTI_PRES_HEIGHT), 0);
        ltiPresReturnUrl = getParam(LTI_PRES_RETURN_URL);
        ltiToolConsumerCode = getParam(LTI_TOOL_CONSUMER_CODE);
        ltiToolConsumerVersion = getParam(LTI_TOOL_CONSUMER_VERSION);
        ltiToolConsumerName = getParam(LTI_TOOL_CONSUMER_NAME);
        ltiToolConsumerEmail = getParam(LTI_TOOL_CONSUMER_EMAIL);
        rawUserRoles = getParam(LTI_USER_ROLES);
        userRoleNumber = makeUserRoleNum(rawUserRoles);
        String[] splitRoles = StringUtils.split(StringUtils.trimToEmpty(rawUserRoles), ",");
        ltiUserRoles = new HashSet<>(Arrays.asList(splitRoles));
        // user displayName requires some special processing
        if (getParam(LTI_USER_NAME_FULL) != null) {
            ltiUserDisplayName = getParam(LTI_USER_NAME_FULL);
        } else if (getParam(LIS_PERSON_PREFIX + "given") != null && getParam(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given") + " " + getParam(LIS_PERSON_PREFIX + "family");
        } else if (getParam(LIS_PERSON_PREFIX + "given") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given");
        } else if (getParam(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "family");
        }
        // store the basics in the session (even though they are already in the security context)
        HttpSession session = this.httpServletRequest.getSession();
        session.setAttribute(LTI_USER_ID, ltiUserId);
        session.setAttribute(LTI_CONTEXT_ID, ltiContextId);
        String normalizedRoleName = LTI_ROLE_GENERAL;
        if (isRoleAdministrator()) {
            normalizedRoleName = LTI_ROLE_ADMIN;
        } else if (isRoleInstructor()) {
            normalizedRoleName = LTI_ROLE_INSTRUCTOR;
        } else if (isRoleLearner()) {
            normalizedRoleName = LTI_ROLE_LEARNER;
        }
        session.setAttribute(LTI_USER_ROLE, normalizedRoleName);
        return complete;
    }

    /**
     * Checks if this LTI request object has a complete set of required LTI data,
     * also sets the #complete variable appropriately
     *
     * @param objects if true then check for complete objects, else just check for complete request params
     * @return true if complete
     */
    protected boolean checkCompleteLTIRequest(boolean objects) {
        if (objects && key != null && context != null && link != null && user != null) {
            complete = true;
        } else if (!objects && ltiConsumerKey != null && ltiContextId != null && ltiLinkId != null && ltiUserId != null) {
            complete = true;
        } else {
            complete = false;
        }
        return complete;
    }

    /**
     * Checks if we have a valid tool registration in the current request
     *
     * @return true if valid, false otherwise
     * @throws IllegalArgumentException if the inputs are invalid
     * @throws IllegalStateException    is the key request is not approved or valid
     */
    public boolean checkValidToolRegistration() {
        assert ltiDataService != null;
        // See if current user is allowed to register a tool
        KeyRequestEntity keyRequestEntity = ltiDataService.findKeyRequest(this);

        if (keyRequestEntity == null) {
            throw new IllegalStateException("Cannot find a key request for the current user");
        }
        if (keyRequestEntity.getState() == 0) {
            throw new IllegalStateException("Your key has not yet been approved");
        }
        if (keyRequestEntity.getState() != 1) {
            throw new IllegalStateException("Your key request was not approved");
        }
        if (keyRequestEntity.getLti() != 2) {
            throw new IllegalStateException("You did not request an LTI 2.0 key");
        }
        // We have a person authorized to use LTI 2.0 on this server

        String regKey;
        String regPass;
        if (LTI_MESSAGE_TYPE_PROXY_REREG.equals(this.ltiMessageType)) {
            regKey = this.ltiConsumerKey;
            regPass = "secret";
        } else if (LTI_MESSAGE_TYPE_PROXY_REG.equals(this.ltiMessageType)) {
            regKey = this.httpServletRequest.getParameter("reg_key");
            regPass = this.httpServletRequest.getParameter("reg_password");
        } else {
            throw new IllegalArgumentException("lti_message_type param is invalid: " + this.ltiMessageType);
        }

        //this.ltiPresReturnUrl

        Map<String, Object> tcProfile;
        String tcProfileJSON;
        String tcProfileURL = getParam(LTI_TOOL_CONSUMER_PROFILE_URL);
        if (StringUtils.length(tcProfileURL) > 1) {
            log.debug("Retrieving profile from " + tcProfileURL);
            String json = this.restTemplate.getForObject(tcProfileURL, String.class);
            try {
                tcProfile = jsonToMap(json);
                tcProfileJSON = json;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unable to parse tc_profile JSON (" + e.getMessage() + "): " + json, e);
            }
        } else {
            throw new IllegalArgumentException("Missing tc_profile_url in the request, cannot register tool...");
        }

        // Find the registration URL
        /* TODO
$oauth_consumer_key = $tc_profile->guid;
$tc_services = $tc_profile->service_offered;
echo("Found ".count($tc_services)." services profile..\n");
if ( count($tc_services) < 1 ) lmsDie("At a minimum, we need the service to register ourself - doh!\n");
// var_dump($tc_services);
$register_url = false;
$result_url = false;
foreach ($tc_services as $tc_service) {
    $formats = $tc_service->{'format'};
    $type = $tc_service->{'@type'};
    $id = $tc_service->{'@id'};
    $actions = $tc_service->action;
    if ( ! (is_array($actions) && in_array('POST', $actions)) ) continue;
    foreach($formats as $format) {
        echo("Service: ".$format." id=".$id."\n");
        if ( $format != "application/vnd.ims.lti.v2.toolproxy+json" ) continue;
        // var_dump($tc_service);
        $register_url = $tc_service->endpoint;
    }
}
if ( $register_url == false ) lmsDie("Must have an application/vnd.ims.lti.v2.toolproxy+json service available in order to do tool_registration.");
         */

        return false;
    }

    public boolean isRoleAdministrator() {
        return (rawUserRoles != null && userRoleNumber >= 2);
    }

    public boolean isRoleInstructor() {
        return (rawUserRoles != null && userRoleNumber >= 1);
    }

    public boolean isRoleLearner() {
        return (rawUserRoles != null && StringUtils.containsIgnoreCase(rawUserRoles, "learner"));
    }

    // STATICS

    /**
     * @param request the incoming request
     * @return true if this is a valid LTI request
     */
    public static boolean isLTIRequest(ServletRequest request) {
        boolean valid = false;
        String ltiVersion = StringUtils.trimToNull(request.getParameter(LTI_VERSION));
        String ltiMessageType = StringUtils.trimToNull(request.getParameter(LTI_MESSAGE_TYPE));
        if (ltiMessageType != null && ltiVersion != null) {
            boolean goodMessageType = LTI_MESSAGE_TYPE_BASIC.equals(ltiMessageType)
                    || LTI_MESSAGE_TYPE_PROXY_REG.equals(ltiMessageType)
                    || LTI_MESSAGE_TYPE_PROXY_REREG.equals(ltiMessageType);
            boolean goodLTIVersion = LTI_VERSION_1P0.equals(ltiVersion)
                    || LTI_VERSION_2P0.equals(ltiVersion);
            valid = goodMessageType && goodLTIVersion;
        }
        // resource_link_id is also required
        return valid;
    }

    /**
     * Creates an LTI composite key which can be used to identify a user session consistently
     *
     * @param request     the incoming request
     * @param sessionSalt the salt (defaults to a big random string)
     * @return the composite string (md5)
     */
    public static String makeLTICompositeKey(HttpServletRequest request, String sessionSalt) {
        if (StringUtils.isBlank(sessionSalt)) {
            sessionSalt = "A7k254A0itEuQ9ndKJuZ";
        }
        String composite = sessionSalt + "::" + request.getParameter(LTI_CONSUMER_KEY) + "::" + request.getParameter(LTI_CONTEXT_ID) + "::" +
                request.getParameter(LTI_LINK_ID) + "::" + request.getParameter(LTI_USER_ID) + "::" + (System.currentTimeMillis() / 1800) +
                request.getHeader("User-Agent") + "::" + request.getContextPath();
        return DigestUtils.md5Hex(composite);
    }

    /**
     * @param rawUserRoles the raw roles string (this could also only be part of the string assuming it is the highest one)
     * @return the number that represents the role (higher is more access)
     */
    public static int makeUserRoleNum(String rawUserRoles) {
        int roleNum = 0;
        if (rawUserRoles != null) {
            String lcRUR = rawUserRoles.toLowerCase();
            if (lcRUR.contains("administrator")) {
                roleNum = 2;
            } else if (lcRUR.contains("instructor")) {
                roleNum = 1;
            }
        }
        return roleNum;
    }

    /**
     * Use Jackson to convert some JSON to a map
     *
     * @param json input JSON
     * @return the map
     * @throws java.lang.IllegalArgumentException if the json is invalid
     */
    public static Map<String, Object> jsonToMap(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException("Invalid json: blank/empty/null string");
        }
        Map<String, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            //noinspection unchecked
            map = mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid json: " + e.getMessage(), e);
        }
        return map;
    }

    /**
     * Use Jackson to check if some JSON is valid
     *
     * @param json a chunk of json
     * @return true if valid
     */
    public static boolean isValidJSON(final String json) {
        boolean valid;
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            JsonParser parser = null;
            try {
                parser = new ObjectMapper().getFactory().createParser(json);
                //noinspection StatementWithEmptyBody
                while (parser.nextToken() != null) {
                }
                valid = true;
            } catch (JsonParseException jpe) {
                valid = false;
            } finally {
                if (parser != null) {
                    parser.close();
                }
            }
        } catch (IOException e) {
            valid = false;
        }
        return valid;
    }

    // GETTERS

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public String getLtiContextId() {
        return ltiContextId;
    }

    public String getLtiConsumerKey() {
        return ltiConsumerKey;
    }

    public String getLtiLinkId() {
        return ltiLinkId;
    }

    public String getLtiMessageType() {
        return ltiMessageType;
    }

    public String getLtiServiceId() {
        return ltiServiceId;
    }

    public String getLtiSourcedid() {
        return ltiSourcedid;
    }

    public String getLtiUserId() {
        return ltiUserId;
    }

    public String getLtiVersion() {
        return ltiVersion;
    }

    public LtiKeyEntity getKey() {
        return key;
    }

    public LtiContextEntity getContext() {
        return context;
    }

    public LtiLinkEntity getLink() {
        return link;
    }

    public LtiMembershipEntity getMembership() {
        return membership;
    }

    public LtiUserEntity getUser() {
        return user;
    }

    public LtiServiceEntity getService() {
        return service;
    }

    public LtiResultEntity getResult() {
        return result;
    }

    public String getLtiContextTitle() {
        return ltiContextTitle;
    }

    public String getLtiContextLabel() {
        return ltiContextLabel;
    }

    public String getLtiLinkTitle() {
        return ltiLinkTitle;
    }

    public String getLtiLinkDescription() {
        return ltiLinkDescription;
    }

    public Locale getLtiPresLocale() {
        return ltiPresLocale;
    }

    public String getLtiPresTarget() {
        return ltiPresTarget;
    }

    public int getLtiPresWidth() {
        return ltiPresWidth;
    }

    public int getLtiPresHeight() {
        return ltiPresHeight;
    }

    public String getLtiPresReturnUrl() {
        return ltiPresReturnUrl;
    }

    public String getLtiToolConsumerCode() {
        return ltiToolConsumerCode;
    }

    public String getLtiToolConsumerVersion() {
        return ltiToolConsumerVersion;
    }

    public String getLtiToolConsumerName() {
        return ltiToolConsumerName;
    }

    public String getLtiToolConsumerEmail() {
        return ltiToolConsumerEmail;
    }

    public String getLtiUserEmail() {
        return ltiUserEmail;
    }

    public String getLtiUserDisplayName() {
        return ltiUserDisplayName;
    }

    public String getLtiUserImageUrl() {
        return ltiUserImageUrl;
    }

    public String getRawUserRoles() {
        return rawUserRoles;
    }

    public Set<String> getLtiUserRoles() {
        return ltiUserRoles;
    }

    public int getUserRoleNumber() {
        return userRoleNumber;
    }

    public int getLoadingUpdates() {
        return loadingUpdates;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isUpdated() {
        return updated;
    }

}
