/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package lti;

import java.io.Serializable;
import java.util.Map;

public class ProxiedLaunch implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, String> params;
    private String launchUrl;

    public Map<String, String> getParams() {
        return params;
    }

    public String getLaunchUrl() {
        return launchUrl;
    }

    public ProxiedLaunch(Map<String, String> params, String launchUrl) {
        super();
        this.params = params;
        this.launchUrl = launchUrl;
    }
}
