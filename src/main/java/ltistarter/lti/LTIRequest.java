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
import ltistarter.repository.AllRepositories;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
     * @param repos   the repos accessor which will be used to load DB data (if possible) for this request
     * @param update  if true then update (or insert) the DB records for this request (else skip DB updating)
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request, AllRepositories repos, boolean update) {
        this(request);
        assert repos != null : "AllRepositories cannot be null";
        this.loadLTIDataFromDB(repos);
        if (update) {
            this.updateLTIDataInDB(repos);
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
        // If there is an appropriate role override variable, we use that role
        rawUserRolesOverride = getParam(USER_ROLE_OVERRIDE);
        if (rawUserRolesOverride != null && rawUserRoles != null) {
            int roleOverrideNum = makeUserRoleNum(rawUserRolesOverride);
            if (roleOverrideNum > userRoleNumber) {
                userRoleNumber = roleOverrideNum;
            }
        }
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
     * Loads up the data which is referenced in this LTI request (assuming it can be found in the DB)
     *
     * @param repos the repos accessor used to load the data (must be set)
     * @return true if any data was loaded OR false if none could be loaded (because no matching data was found or the input keys are not set)
     */
    @Transactional
    public boolean loadLTIDataFromDB(AllRepositories repos) {
        assert repos != null;
        loaded = false;
        if (ltiConsumerKey == null) {
            // don't even attempt this without a key, it's pointless
            log.info("LTIload: No key to load results for");
            return false;
        }
        boolean includesService = (ltiServiceId != null);
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
        Query q = repos.entityManager.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("key", BaseEntity.makeSHA256(ltiConsumerKey));
        q.setParameter("context", BaseEntity.makeSHA256(ltiContextId));
        q.setParameter("link", BaseEntity.makeSHA256(ltiLinkId));
        q.setParameter("user", BaseEntity.makeSHA256(ltiUserId));
        if (includesService) {
            q.setParameter("service", BaseEntity.makeSHA256(ltiServiceId));
        }
        if (includesSourcedid) {
            q.setParameter("sourcedid", BaseEntity.makeSHA256(ltiSourcedid));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            log.info("LTIload: No results found for key=" + ltiConsumerKey);
        } else {
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
            // check if the loading resulted in a complete set of LTI data
            checkCompleteLTIRequest(true);
            loaded = true;
            log.info("LTIload: loaded data for key=" + ltiConsumerKey + " and context=" + ltiContextId + ", complete=" + complete);
        }
        return loaded;
    }

    /**
     * Attempts to insert or update the various LTI launch data (or other data that is part of this request)
     *
     * @param repos the repos accessor used to load the data (must be set)
     * @return the number of changes (inserts or updates) that occur
     */
    @Transactional
    public int updateLTIDataInDB(AllRepositories repos) {
        assert repos != null : "access to the repos is required";
        assert loaded : "Data must be loaded before it can be updated";

        assert key != null : "Key data must not be null to update data";
        repos.entityManager.merge(key); // reconnect the key object for this transaction

        int inserts = 0;
        int updates = 0;
        if (context == null && ltiContextId != null) {
            LtiContextEntity newContext = new LtiContextEntity(ltiContextId, key, ltiContextTitle, null);
            context = repos.contexts.save(newContext);
            inserts++;
            log.info("LTIupdate: Inserted context id=" + ltiContextId);
        } else if (context != null) {
            repos.entityManager.merge(context); // reconnect object for this transaction
            ltiContextId = context.getContextKey();
            log.info("LTIupdate: Reconnected existing context id=" + ltiContextId);
        }

        if (link == null && ltiLinkId != null) {
            LtiLinkEntity newLink = new LtiLinkEntity(ltiLinkId, context, ltiLinkTitle);
            link = repos.links.save(newLink);
            inserts++;
            log.info("LTIupdate: Inserted link id=" + ltiLinkId);
        } else if (link != null) {
            repos.entityManager.merge(link); // reconnect object for this transaction
            ltiLinkId = link.getLinkKey();
            log.info("LTIupdate: Reconnected existing link id=" + ltiLinkId);
        }

        if (user == null && ltiUserId != null) {
            LtiUserEntity newUser = new LtiUserEntity(ltiUserId, null);
            newUser.setDisplayName(ltiUserDisplayName);
            newUser.setEmail(ltiUserEmail);
            user = repos.users.save(newUser);
            inserts++;
            log.info("LTIupdate: Inserted user id=" + ltiUserId);
        } else if (user != null) {
            repos.entityManager.merge(user); // reconnect object for this transaction
            ltiUserId = user.getUserKey();
            log.info("LTIupdate: Reconnected existing user id=" + ltiUserId);
        }

        if (membership == null && context != null && user != null) {
            LtiMembershipEntity newMember = new LtiMembershipEntity(context, user, userRoleNumber);
            membership = repos.members.save(newMember);
            inserts++;
            log.info("LTIupdate: Inserted membership id=" + newMember.getMembershipId() + ", role=" + newMember.getRole() + ", user=" + ltiUserId + ", context=" + ltiContextId);
        } else if (membership != null) {
            repos.entityManager.merge(membership); // reconnect object for this transaction
            ltiUserId = user.getUserKey();
            ltiContextId = context.getContextKey();
            log.info("LTIupdate: Reconnected existing membership id=" + membership.getMembershipId());
        }

        // We need to handle the case where the service URL changes but we already have a sourcedid
        boolean serviceCreated = false;
        if (service == null && ltiServiceId != null && ltiSourcedid != null) {
            LtiServiceEntity newService = new LtiServiceEntity(ltiServiceId, key, null);
            service = repos.services.save(newService);
            inserts++;
            serviceCreated = true;
            log.info("LTIupdate: Inserted service id=" + ltiServiceId);
        } else if (service != null) {
            repos.entityManager.merge(service); // reconnect object for this transaction
            ltiServiceId = service.getServiceKey();
            log.info("LTIupdate: Reconnected existing service id=" + ltiServiceId);
        }

        // If we just created a new service entry but we already had a result entry, update it
        if (serviceCreated && result != null && ltiServiceId != null && ltiSourcedid != null) {
            repos.entityManager.merge(result); // reconnect object for this transaction
            result.setSourcedid(ltiSourcedid);
            repos.results.save(result);
            inserts++;
            log.info("LTIupdate: Updated existing result id=" + result.getResultId() + ", sourcedid=" + ltiSourcedid);
        }

        // If we don't have a result but do have a service - link them together
        if (result == null
                && service != null && user != null && link != null
                && ltiServiceId != null && ltiSourcedid != null) {
            LtiResultEntity newResult = new LtiResultEntity(ltiSourcedid, user, link, null, null);
            result = repos.results.save(newResult);
            inserts++;
            log.info("LTIupdate: Inserted result id=" + result.getResultId());
        } else if (result != null) {
            repos.entityManager.merge(result); // reconnect object for this transaction
            ltiSourcedid = result.getSourcedid();
        }

        // If we don't have a result and do not have a service - just store the result (prep for LTI 2.0)
        if (result == null && service == null && user != null && link != null && ltiSourcedid != null) {
            LtiResultEntity newResult = new LtiResultEntity(ltiSourcedid, user, link, null, null);
            result = repos.results.save(newResult);
            inserts++;
            log.info("LTIupdate: Inserted LTI 2 result id=" + result.getResultId() + ", sourcedid=" + ltiSourcedid);
        }

        // Here we handle updates to sourcedid
        if (result != null && ltiSourcedid != null && !ltiSourcedid.equals(result.getSourcedid())) {
            result.setSourcedid(ltiSourcedid);
            result = repos.results.save(result);
            updates++;
            log.info("LTIupdate: Updated result (id=" + result.getResultId() + ") sourcedid=" + ltiSourcedid);
        }

        // Next we handle updates to context_title, link_title, user_displayname, user_email, or role

        if (ltiContextTitle != null && context != null && !ltiContextTitle.equals(context.getTitle())) {
            context.setTitle(ltiContextTitle);
            context = repos.contexts.save(context);
            updates++;
            log.info("LTIupdate: Updated context (id=" + context.getContextId() + ") title=" + ltiContextTitle);
        }

        if (ltiLinkTitle != null && link != null && !ltiLinkTitle.equals(link.getTitle())) {
            link.setTitle(ltiLinkTitle);
            link = repos.links.save(link);
            updates++;
            log.info("LTIupdate: Updated link (id=" + link.getLinkKey() + ") title=" + ltiLinkTitle);
        }

        boolean userChanged = false;
        if (ltiUserDisplayName != null && user != null && !ltiUserDisplayName.equals(user.getDisplayName())) {
            user.setDisplayName(ltiUserDisplayName);
        }
        if (ltiUserEmail != null && user != null && !ltiUserEmail.equals(user.getEmail())) {
            user.setEmail(ltiUserEmail);
        }
        if (userChanged) {
            user = repos.users.save(user);
            updates++;
            log.info("LTIupdate: Updated user (id=" + user.getUserKey() + ") name=" + ltiUserDisplayName + ", email=" + ltiUserEmail);
        }

        if (rawUserRoles != null && userRoleNumber != membership.getRole()) {
            membership.setRole(userRoleNumber);
            membership = repos.members.save(membership);
            updates++;
            log.info("LTIupdate: Updated membership (id=" + membership.getMembershipId() + ", user=" + ltiUserId + ", context=" + ltiContextId + ") roles=" + rawUserRoles + ", role=" + userRoleNumber);
        }

        // need to recheck and see if we are complete now
        checkCompleteLTIRequest(true);

        loadingUpdates = inserts + updates;
        updated = true;
        log.info("LTIupdate: changes=" + loadingUpdates + ", inserts=" + inserts + ", updates=" + updates);
        return loadingUpdates;
    }

    /**
     * Checks if this LTI request object has a complete set of required LTI data,
     * also sets the #complete variable appropriately
     *
     * @param objects if true then check for complete objects, else just check for complete request params
     * @return true if complete
     */
    private boolean checkCompleteLTIRequest(boolean objects) {
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
