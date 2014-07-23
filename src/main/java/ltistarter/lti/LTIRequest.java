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

import ltistarter.model.BaseEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * LTI Request object holds all the details for a valid LTI request
 * (including data populated on the validated launch)
 */
@SuppressWarnings("deprecation")
public class LTIRequest {

    final static Logger log = LoggerFactory.getLogger(LTIRequest.class);

    private HttpServletRequest httpServletRequest;

    String ltiContext;
    String ltiKey;
    String ltiLink;
    String ltiMessageType;
    String ltiService;
    String ltiSourcedid;
    String ltiUser;
    String ltiVersion;

    public LTIRequest(HttpServletRequest request) {
        this.httpServletRequest = request;
        // extract the typical LTI data from the request
        if (!isLTIRequest(request)) {
            throw new IllegalStateException("Request is not an LTI request");
        }
        ltiContext = request.getParameter("context_id");
        ltiKey = request.getParameter("key");
        ltiLink = request.getParameter("link_id");
        ltiMessageType = request.getParameter("lti_message_type");
        ltiService = request.getParameter("service");
        ltiSourcedid = request.getParameter("sourcedid");
        ltiUser = request.getParameter("user_id");
        ltiVersion = request.getParameter("lti_version");
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * @param request the incoming request
     * @return true if this is a valid LTI request
     */
    public static boolean isLTIRequest(ServletRequest request) {
        boolean valid = false;
        String ltiVersion = request.getParameter("lti_version");
        String ltiMessageType = request.getParameter("lti_message_type");
        if (ltiMessageType != null && ltiVersion != null) {
            boolean goodMessageType = "basic-lti-launch-request".equals(ltiMessageType)
                    || "ToolProxyReregistrationRequest".equals(ltiMessageType);
            boolean goodLTIVersion = "LTI-1p0".equals(ltiVersion)
                    || "LTI-2p0".equals(ltiVersion);
            valid = goodMessageType && goodLTIVersion;
        }
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
        String composite = sessionSalt + "::" + request.getParameter("key") + "::" + request.getParameter("context_id") + "::" +
                request.getParameter("link_id") + "::" + request.getParameter("user_id") + "::" + (System.currentTimeMillis() / 1800) +
                request.getHeader("User-Agent") + "::" + request.getContextPath();
        return DigestUtils.md5Hex(composite);
    }

    public Object loadLTIDataFromDB(EntityManager entityManager, HttpServletRequest request, boolean includeProfile) {
        boolean includesService = (ltiService != null);
        boolean includesSourcedid = (ltiSourcedid != null);
        assert StringUtils.isNotBlank(ltiKey) : "LTI request must contain key";
        assert StringUtils.isNotBlank(ltiContext) : "LTI request must contain context_id";
        assert StringUtils.isNotBlank(ltiLink) : "LTI request must contain link_id";
        assert StringUtils.isNotBlank(ltiUser) : "LTI request must contain user_id";

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT k.keyId, k.keyKey, k.secret, c.contextId, c.title AS contextTitle, l.linkId, l.title AS linkTitle, u.userId, u.displayName AS userDisplayName, u.email AS userEmail, u.subscribe, u.userSha256, m.membershipId, m.role, m.roleOverride");

        if (includeProfile) {
            sb.append(", p.profileId, p.displayName AS profileDisplayName, p.email AS profileEmail, p.subscribe AS profileSubscribe");
        }
        if (includesService) {
            sb.append(", s.serviceId, s.serviceKey AS service");
        }
        if (includesSourcedid) {
            sb.append(", r.resultId, r.sourcedid, r.grade");
        }

        sb.append("FROM LtiKeyEntity k " +
                "LEFT JOIN k.contexts c ON c.contextSha256 = :context " + // LtiContextEntity
                "LEFT JOIN LtiLinkEntity l ON c.context_id = l.context_id AND l.linkSha256 = :link " + // LtiLinkEntity
                "LEFT JOIN LtiUserEntity u ON k.key_id = u.key_id AND u.userSha256 = :user " + // LtiUserEntity
                "LEFT JOIN LtiMembershipEntity m ON u.user_id = m.user_id AND c.context_id = m.context_id" // LtiMembershipEntity
        );

        if (includeProfile) {
            sb.append(" LEFT JOIN ProfileEntity AS p ON u.profileId = p.profileId"); // ProfileEntity
        }
        if (includesService) {
            sb.append(" LEFT JOIN LtiServiceEntity AS s ON k.key_id = s.key_id AND s.service_sha256 = :service"); // LtiServiceEntity
        }
        if (includesSourcedid) {
            sb.append(" LEFT JOIN LtiResultEntity AS r ON u.user_id = r.user_id AND l.link_id = r.link_id"); // LtiResultEntity
        }

        sb.append(" WHERE k.keySha256 = :key");
        String sql = sb.toString();
        Query q = entityManager.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("context", BaseEntity.makeSHA256(ltiContext));
        q.setParameter("link", BaseEntity.makeSHA256(ltiLink));
        q.setParameter("user", BaseEntity.makeSHA256(ltiUser));
        q.setParameter("key", BaseEntity.makeSHA256(ltiKey));
        // TODO set optional params
        List<Object[]> rows = q.getResultList();

        /*
        // echo(sql);
        $parms = array(
                ':key' => lti_sha256(request.getParameter("key")),
                ':context' => lti_sha256(request.getParameter("context_id")),
                ':link' => lti_sha256(request.getParameter("link_id")),
                ':user' => lti_sha256(request.getParameter("user_id")));

        if ( request.getParameter("service") ) {
            $parms[':service") = lti_sha256(request.getParameter("service"));
        }

        $row = pdoRowDie($pdo, sql, $parms);
        */
        return null; // TODO
    }

    public String getLtiContext() {
        return ltiContext;
    }

    public String getLtiKey() {
        return ltiKey;
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

}
