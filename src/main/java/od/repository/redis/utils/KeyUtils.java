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
package od.repository.redis.utils;

import javax.annotation.PostConstruct;

import od.RedisConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("redis")
public class KeyUtils {

    @Autowired
    private RedisConfiguration redisConfiguration;
    private String namespace;

    @PostConstruct
    public void setup() {
        namespace = redisConfiguration.getNamespace();
    }

    public String session(String id) {
        return String.format("%s:session:%s", namespace, id);
    }

    public String contextMapping(String id) {
        return String.format("%s:contextMapping:%s", namespace, id);
    }

    public String contextMappingByKeyAndContext(String key, String context) {
        return String.format("%s:contextMapping:key:%s:context:%s", namespace, key, context);
    }

    public String contextMappingDashboards(String id) {
        return String.format("%s:contextMapping:%s:Dashboards", namespace, id);
    }

    public String contextMappingByKeyAndContextDashboards(String key, String context) {
        return String.format("%s:contextMapping:key:%s:context:%s:Dashboards", namespace, key, context);
    }

    public String ltiLaunchRequest(String id) {
        return String.format("%s:launchData:%s:lti", namespace, id);
    }

    public String ltiLaunchRequestExt(String id) {
        return String.format("%s:launchData:%s:lti:ext", namespace, id);
    }

    public String ltiLaunchRequestExtra(String id) {
        return String.format("%s:launchData:%s:lti:extra", namespace, id);
    }

    public String ltiLaunchRequestCustom(String id) {
        return String.format("%s:launchData:%s:lti:custom", namespace, id);
    }

    public String globalSessionId() {
        return String.format("%s:global:sessionId", namespace);
    }

    public String globalContextMappingId() {
        return String.format("%s:global:contextMappingId", namespace);
    }
}
