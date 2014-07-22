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
}
