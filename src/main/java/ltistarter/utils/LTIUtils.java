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
package ltistarter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static boolean isLTIRequest(HttpServletRequest request) {
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

}
