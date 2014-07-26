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

import ltistarter.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * LTI Request object holds all the details for a valid LTI request
 * (including data populated on the validated launch)
 *
 * This basically does everything in lti_db.php from tsugi (except the OAuth stuff, that is handled by spring security)
 *
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
    public static final String LTI_USER_ID = "user_id";
    public static final String LTI_USER_EMAIL = "lis_person_contact_email_primary";
    public static final String LTI_USER_NAME_FULL = LIS_PERSON_PREFIX + "full";
    public static final String LTI_USER_IMAGE_URL = "user_image";
    public static final String LTI_USER_ROLES = "roles";
    public static final String LTI_VERSION = "lti_version";
    public static final String USER_ROLE_OVERRIDE = "role_override";

    public static final String LTI_MESSAGE_TYPE_BASIC = "basic-lti-launch-request";
    public static final String LTI_MESSAGE_TYPE_PROXY_REG = "ToolProxyReregistrationRequest";
    public static final String LTI_VERSION_1P0 = "LTI-1p0";
    public static final String LTI_VERSION_2P0 = "LTI-2p0";

    HttpServletRequest httpServletRequest;

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
     * @param request an http servlet request
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request) {
        assert request != null : "cannot make an LtiRequest without a request";
        this.httpServletRequest = request;
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
                    || LTI_MESSAGE_TYPE_PROXY_REG.equals(ltiMessageType);
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
