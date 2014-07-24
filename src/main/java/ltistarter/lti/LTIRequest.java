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
        ltiContextId = getParam(LTI_CONTEXT_ID);
        ltiLinkId = getParam(LTI_LINK_ID);
        ltiUserId = getParam(LTI_USER_ID);
        if (ltiConsumerKey != null && ltiContextId != null && ltiLinkId != null && ltiUserId != null) {
            complete = true;
        }
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
        String[] splitRoles = StringUtils.split(StringUtils.trimToEmpty(rawUserRoles), ",");
        ltiUserRoles = new HashSet<>(Arrays.asList(splitRoles));
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
     * @param repos the repos accessor which will be used to load DB data (if possible) for this request
     * @throws IllegalStateException if this is not an LTI request
     */
    public LTIRequest(HttpServletRequest request, AllRepositories repos) {
        this(request);
        assert repos != null : "AllRepositories cannot be null";
        this.loadLTIDataFromDB(repos);
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
        return (rawUserRoles != null && rawUserRoles.toLowerCase().contains("administrator"));
    }

    public boolean isRoleInstructor() {
        return (rawUserRoles != null && rawUserRoles.toLowerCase().contains("instructor"));
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

    @Transactional
    public void updateLTIDataInDB(AllRepositories repos) {
        assert repos != null : "access to the repos is required";
        assert loaded : "Data must be loaded before it can be updated";
        assert key != null : "Key data must not be null to update data";
        repos.entityManager.merge(key); // reconnect the key object for this transaction

        //$actions = array();
        if (context == null && ltiContextId != null) {
            LtiContextEntity newContext = new LtiContextEntity(ltiContextId, key, ltiContextTitle, null);
            context = repos.contexts.save(newContext);
            log.info("LTIupdate: Inserted context id=" + ltiContextId);
        } else if (context != null) {
            repos.entityManager.merge(context); // reconnect object for this transaction
            ltiContextId = context.getContextKey();
            log.info("LTIupdate: Reconnected existing context id=" + ltiContextId);
        }

        if (link == null && ltiLinkId != null) {
            LtiLinkEntity newLink = new LtiLinkEntity(ltiLinkId, context, ltiLinkTitle);
            link = repos.links.save(newLink);
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
            log.info("LTIupdate: Inserted user id=" + ltiUserId);
        } else if (user != null) {
            repos.entityManager.merge(user); // reconnect object for this transaction
            ltiUserId = user.getUserKey();
            log.info("LTIupdate: Reconnected existing user id=" + ltiUserId);
        }

        if (membership == null && context != null && user != null) {
            int roleNum = (isRoleInstructor() || isRoleAdministrator()) ? 1 : 0;
            LtiMembershipEntity newMember = new LtiMembershipEntity(context, user, roleNum);
            membership = repos.members.save(newMember);
            log.info("LTIupdate: Inserted membership id=" + newMember.getMembershipId() + ", role=" + newMember.getRole() + ", user=" + ltiUserId + ", context=" + ltiContextId);
        } else if (membership != null) {
            repos.entityManager.merge(membership); // reconnect object for this transaction
            ltiUserId = user.getUserKey();
            ltiContextId = context.getContextKey();
            log.info("LTIupdate: Reconnected existing membership id=" + membership.getMembershipId());
        }

        /*
        // We need to handle the case where the service URL changes but we already have a sourcedid
        $oldserviceid = service_id'];
        if ( service_id === null && $post['service'] && $post['sourcedid'] ) {
            String sql = "INSERT INTO {$p}lti_service
            ( service_key, service_sha256, key_id, created_at, updated_at ) VALUES
                    ( :service_key, :service_sha256, :key_id, NOW(), NOW() )";
            pdoQueryDie($pdo, $sql, array(
                            ':service_key' => $post['service'],
                    ':service_sha256' => lti_sha256($post['service']),
                    ':key_id' => key_id']));
            service_id = $pdo->lastInsertId();
            service = $post['service'];
            $actions[] = "=== Inserted service id=".service_id']." ".$post['service'];
        }

        // If we just created a new service entry but we already had a result entry, update it
        if ( $oldserviceid === null && result_id'] !== null && service_id'] !== null && $post['service'] && $post['sourcedid'] ) {
            String sql = "UPDATE {$p}lti_result SET service_id = :service_id WHERE result_id = :result_id";
            pdoQueryDie($pdo, $sql, array(
                            ':service_id' => service_id'],
                    ':result_id' => result_id']));
            $actions[] = "=== Updated result id=".result_id']." service=".service_id']." ".$post['sourcedid'];
        }

        // If we don'have a result but do have a service - link them together
        if ( result_id === null && service_id'] !== null && $post['service'] && $post['sourcedid'] ) {
            String sql = "INSERT INTO {$p}lti_result
            ( sourcedid, sourcedid_sha256, service_id, link_id, user_id, created_at, updated_at ) VALUES
                    ( :sourcedid, :sourcedid_sha256, :service_id, :link_id, :user_id, NOW(), NOW() )";
            pdoQueryDie($pdo, $sql, array(
                            ':sourcedid' => $post['sourcedid'],
                    ':sourcedid_sha256' => lti_sha256($post['sourcedid']),
                    ':service_id' => service_id'],
                    ':link_id' => link_id'],
                    ':user_id' => user_id']));
            result_id = $pdo->lastInsertId();
            sourcedid = $post['sourcedid'];
            $actions[] = "=== Inserted result id=".result_id']." service=".service_id']." ".$post['sourcedid'];
        }

        // If we don'have a result and do not have a service - just store the result (prep for LTI 2.0)
        if ( result_id === null && service_id === null && ! $post['service'] && $post['sourcedid'] ) {
            String sql = "INSERT INTO {$p}lti_result
            ( sourcedid, sourcedid_sha256, link_id, user_id, created_at, updated_at ) VALUES
                    ( :sourcedid, :sourcedid_sha256, :link_id, :user_id, NOW(), NOW() )";
            pdoQueryDie($pdo, $sql, array(
                            ':sourcedid' => $post['sourcedid'],
                    ':sourcedid_sha256' => lti_sha256($post['sourcedid']),
                    ':link_id' => link_id'],
                    ':user_id' => user_id']));
            result_id = $pdo->lastInsertId();
            $actions[] = "=== Inserted LTI 2.0 result id=".result_id']." service=".service_id']." ".$post['sourcedid'];
        }

        // Here we handle updates to sourcedid
        if ( result_id'] != null && $post['sourcedid'] != null && $post['sourcedid'] != sourcedid'] ) {
            String sql = "UPDATE {$p}lti_result
            SET sourcedid = :sourcedid, sourcedid_sha256 = :sourcedid_sha256
            WHERE result_id = :result_id";
            pdoQueryDie($pdo, $sql, array(
                            ':sourcedid' => $post['sourcedid'],
                    ':sourcedid_sha256' => lti_sha256($post['sourcedid']),
                    ':result_id' => result_id']));
            sourcedid = $post['sourcedid'];
            $actions[] = "=== Updated sourcedid=".sourcedid'];
        }

        // Here we handle updates to context_title, link_title, user_displayname, user_email, or role
        if ( isset($post['context_title']) && $post['context_title'] != context_title'] ) {
            String sql = "UPDATE {$p}lti_context SET title = :title WHERE context_id = :context_id";
            pdoQueryDie($pdo, $sql, array(
                            ':title' => $post['context_title'],
                    ':context_id' => context_id']));
            context_title = $post['context_title'];
            $actions[] = "=== Updated context=".context_id']." title=".$post['context_title'];
        }

        if ( isset($post['link_title']) && $post['link_title'] != link_title'] ) {
            String sql = "UPDATE {$p}lti_link SET title = :title WHERE link_id = :link_id";
            pdoQueryDie($pdo, $sql, array(
                            ':title' => $post['link_title'],
                    ':link_id' => link_id']));
            link_title = $post['link_title'];
            $actions[] = "=== Updated link=".link_id']." title=".$post['link_title'];
        }

        if ( isset($post['user_displayname']) && $post['user_displayname'] != user_displayname'] && strlen($post['user_displayname']) > 0 ) {
            String sql = "UPDATE {$p}lti_user SET displayname = :displayname WHERE user_id = :user_id";
            pdoQueryDie($pdo, $sql, array(
                            ':displayname' => $post['user_displayname'],
                    ':user_id' => user_id']));
            user_displayname = $post['user_displayname'];
            $actions[] = "=== Updated user=".user_id']." displayname=".$post['user_displayname'];
        }

        if ( isset($post['user_email']) && $post['user_email'] != user_email'] && strlen($post['user_email']) > 0 ) {
            String sql = "UPDATE {$p}lti_user SET email = :email WHERE user_id = :user_id";
            pdoQueryDie($pdo, $sql, array(
                            ':email' => $post['user_email'],
                    ':user_id' => user_id']));
            user_email = $post['user_email'];
            $actions[] = "=== Updated user=".user_id']." email=".$post['user_email'];
        }

        if ( isset($post['role']) && $post['role'] != role'] ) {
            String sql = "UPDATE {$p}lti_membership SET role = :role WHERE membership_id = :membership_id";
            pdoQueryDie($pdo, $sql, array(
                            ':role' => $post['role'],
                    ':membership_id' => membership_id']));
            role = $post['role'];
            $actions[] = "=== Updated membership=".membership_id']." role=".$post['role'];
        }
        */

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

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isComplete() {
        return complete;
    }

}
