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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * LTI Request object holds all the details for a valid LTI request
 * (including data populated on the validated launch)
 *
 * This basically does everything in lti_db.php from tsugi (except the OAuth stuff, that is handled by spring security)
 *
 */
@SuppressWarnings("deprecation")
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

    // these are populated on construct
    String ltiContext;
    String ltiContextTitle;
    String ltiContextLabel;
    String ltiConsumerKey;
    String ltiLink;
    String ltiLinkTitle;
    String ltiLinkDescription;
    Locale ltiPresLocale;
    String ltiPresTarget;
    int ltiPresWidth;
    int ltiPresHeight;
    String ltiPresReturnUrl;
    String ltiMessageType;
    String ltiService;
    String ltiSourcedid;
    String ltiToolConsumerCode;
    String ltiToolConsumerVersion;
    String ltiToolConsumerName;
    String ltiToolConsumerEmail;
    String ltiUser;
    String ltiUserEmail;
    String ltiUserDisplayName;
    String ltiUserImageUrl;
    String ltiUserRoles;
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
        ltiMessageType = getParam(LTI_MESSAGE_TYPE);
        ltiVersion = getParam(LTI_VERSION);
        // These 4 really need to be populated for this LTI request to make any sense...
        ltiConsumerKey = getParam(LTI_CONSUMER_KEY);
        ltiContext = getParam(LTI_CONTEXT_ID);
        ltiLink = getParam(LTI_LINK_ID);
        ltiUser = getParam(LTI_USER_ID);
        if (ltiConsumerKey != null && ltiContext != null && ltiLink != null && ltiUser != null) {
            complete = true;
        }
        // OPTIONAL fields below
        ltiService = getParam(LTI_SERVICE);
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
        ltiUserRoles = getParam(LTI_USER_ROLES);
        // user displayName requires some trickyness
        if (request.getParameter(LTI_USER_NAME_FULL) != null) {
            ltiUserDisplayName = getParam(LTI_USER_NAME_FULL);
        } else if (request.getParameter(LIS_PERSON_PREFIX + "given") != null && request.getParameter(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given") + " " + getParam(LIS_PERSON_PREFIX + "family");
        } else if (request.getParameter(LIS_PERSON_PREFIX + "given") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "given");
        } else if (request.getParameter(LIS_PERSON_PREFIX + "family") != null) {
            ltiUserDisplayName = getParam(LIS_PERSON_PREFIX + "family");
        }
    }

    /**
     * @param request       an http servlet request
     * @param entityManager the EM which will be used to load DB data (if possible) for this request
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request, EntityManager entityManager) {
        this(request);
        assert entityManager != null : "entityManager cannot be null";
        this.loadLTIDataFromDB(entityManager);
    }

    /**
     * @param paramName the request parameter name
     * @return the value of the parameter OR null if there is none
     */
    public String getParam(String paramName) {
        String value = null;
        if (paramName != null) {
            value = StringUtils.trimToNull(this.httpServletRequest.getParameter(paramName));
        }
        return value;
    }

    public boolean isRoleAdministrator() {
        return (ltiUserRoles != null && ltiUserRoles.toLowerCase().contains("administrator"));
    }

    public boolean isRoleInstructor() {
        return (ltiUserRoles != null && ltiUserRoles.toLowerCase().contains("instructor"));
    }

    /**
     * Loads up the data which is referenced in this LTI request (assuming it can be found in the DB)
     *
     * @param entityManager the EM used to load the data (must be set)
     * @return true if any data was loaded OR false if none could be loaded (because no matching data was found or the input keys are not set)
     */
    @Transactional
    public boolean loadLTIDataFromDB(EntityManager entityManager) {
        assert entityManager != null;
        loaded = false;
        if (ltiConsumerKey == null) {
            // don't even attempt this without a key, it's pointless
            return false;
        }
        boolean includesService = (ltiService != null);
        boolean includesSourcedid = (ltiSourcedid != null);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT k, c, l, m, u"); //k.keyId, k.keyKey, k.secret, c.contextId, c.title AS contextTitle, l.linkId, l.title AS linkTitle, u.userId, u.displayName AS userDisplayName, u.email AS userEmail, u.subscribe, u.userSha256, m.membershipId, m.role, m.roleOverride"); // 15

        if (includesService) {
            sb.append(", s"); //, s.serviceId, s.serviceKey AS service"); // 2
        }
        if (includesSourcedid) {
            sb.append(", r"); //, r.resultId, r.sourcedid, r.grade"); // 3
        }
        /*
        if (includeProfile) {
            sb.append(", p"); //", p.profileId, p.displayName AS profileDisplayName, p.email AS profileEmail, p.subscribe AS profileSubscribe"); // 4
        }*/

        sb.append(" FROM LtiKeyEntity k " +
                "LEFT JOIN k.contexts c ON c.contextSha256 = :context " + // LtiContextEntity
                "LEFT JOIN c.links l ON l.linkSha256 = :link " + // LtiLinkEntity
                "LEFT JOIN c.memberships m " + // LtiMembershipEntity
                "LEFT JOIN m.user u ON u.userSha256 = :user " // LtiUserEntity
        );

        if (includesService) {
            sb.append(" LEFT JOIN k.services s ON s.serviceSha256 = :service"); // LtiServiceEntity
        }
        if (includesSourcedid) {
            sb.append(" LEFT JOIN u.results r ON r.sourcedidSha256 = :sourcedid"); // LtiResultEntity
        }
        /*
        if (includeProfile) {
            sb.append(" LEFT JOIN u.profile p"); // ProfileEntity
        }*/
        sb.append(" WHERE k.keySha256 = :key AND (m IS NULL OR (m.context = c AND m.user = u))");
        /*
        if (includeProfile) {
            sb.append(" AND (u IS NULL OR u.profile = p)");
        }*/

        String sql = sb.toString();
        Query q = entityManager.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("key", BaseEntity.makeSHA256(ltiConsumerKey));
        q.setParameter("context", BaseEntity.makeSHA256(ltiContext));
        q.setParameter("link", BaseEntity.makeSHA256(ltiLink));
        q.setParameter("user", BaseEntity.makeSHA256(ltiUser));
        if (includesService) {
            q.setParameter("service", BaseEntity.makeSHA256(ltiService));
        }
        if (includesSourcedid) {
            q.setParameter("sourcedid", BaseEntity.makeSHA256(ltiSourcedid));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows != null && !rows.isEmpty()) {
            // k, c, l, m, u, s, r
            Object[] row = rows.get(0);
            if (row.length > 0) key = (LtiKeyEntity) row[0];
            if (row.length > 1) context = (LtiContextEntity) row[1];
            if (row.length > 2) link = (LtiLinkEntity) row[2];
            if (row.length > 3) membership = (LtiMembershipEntity) row[3];
            if (row.length > 4) user = (LtiUserEntity) row[4];
            if (includesService && includesSourcedid) {
                if (row.length > 5) service = (LtiServiceEntity) row[5];
                if (row.length > 6) result = (LtiResultEntity) row[6];
            } else if (includesService) {
                if (row.length > 5) service = (LtiServiceEntity) row[5];
            } else if (includesSourcedid) {
                if (row.length > 5) result = (LtiResultEntity) row[5];
            }
            loaded = true;
            return true;
        }
        return false;
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

    // GETTERS

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public String getLtiContext() {
        return ltiContext;
    }

    public String getLtiConsumerKey() {
        return ltiConsumerKey;
    }

    public String getLtiLink() {
        return ltiLink;
    }

    public String getLtiMessageType() {
        return ltiMessageType;
    }

    public String getLtiService() {
        return ltiService;
    }

    public String getLtiSourcedid() {
        return ltiSourcedid;
    }

    public String getLtiUser() {
        return ltiUser;
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

    public String getLtiUserRoles() {
        return ltiUserRoles;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isComplete() {
        return complete;
    }

}
