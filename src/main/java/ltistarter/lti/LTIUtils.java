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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * LTI utilities
 */
public class LTIUtils {

    final static Logger log = LoggerFactory.getLogger(LTIUtils.class);

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

    public static Object loadLTIDataFromDB(EntityManager entityManager, HttpServletRequest request, boolean includeProfile) {
        String sql = "SELECT k.key_id, k.key_key, k.secret, c.context_id, c.title AS context_title, l.link_id, l.title AS link_title, u.user_id, u.displayname AS user_displayname, u.email AS user_email, u.subscribe AS subscribe, u.user_sha256 AS user_sha256, m.membership_id, m.role, m.role_override";

        if (includeProfile) {
            sql += ", p.profile_id, p.displayname AS profile_displayname, p.email AS profile_email, p.subscribe AS profile_subscribe";
        }

        if (request.getParameter("service") != null) {
            sql += ", s.service_id, s.service_key AS service";
        }

        if (request.getParameter("sourcedid") != null) {
            sql += ", r.result_id, r.sourcedid, r.grade";
        }

        sql += "\nFROM LtiKeyEntity AS k LEFT JOIN LtiContextEntity AS c ON k.key_id = c.key_id AND c.context_sha256 = :context LEFT JOIN LtiLinkEntity AS l ON c.context_id = l.context_id AND l.link_sha256 = :link LEFT JOIN LtiUserEntity AS u ON k.key_id = u.key_id AND u.user_sha256 = :user LEFT JOIN LtiMembershipEntity AS m ON u.user_id = m.user_id AND c.context_id = m.context_id";

        if (includeProfile) {
            sql += " LEFT JOIN ProfileEntity AS p ON u.profile_id = p.profile_id";
        }

        if (request.getParameter("service") != null) {
            sql += " LEFT JOIN LtiServiceEntity AS s ON k.key_id = s.key_id AND s.service_sha256 = :service";
        }
        if (request.getParameter("sourcedid") != null) {
            sql += " LEFT JOIN LtiResultEntity AS r ON u.user_id = r.user_id AND l.link_id = r.link_id";
        }
        sql += " WHERE k.key_sha256 = :key LIMIT 1";

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

}
