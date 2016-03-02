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
/**
 *
 */
package od.framework.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lti.LaunchRequest;

/**
 * @author ggilbert
 *
 */
public class Session extends OpenDashboardModel {
    private Map<String, LaunchRequest> data = new LinkedHashMap<>();;
    private long timestamp;

    public Map<String, LaunchRequest> getData() {
        return data;
    }
    public void setData(Map<String, LaunchRequest> data) {
        this.data = data;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
