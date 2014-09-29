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
package ltistarter.services;

import ltistarter.model.ltiproxy.LtiProxyConfig;
import ltistarter.repository.ltiproxy.LtiProxyConfigRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This manages the LtiProxyConfig data.
 * Necessary to get appropriate TX handling and service management
 */
@Component
public class LtiProxyConfigService {

    final static Logger log = LoggerFactory.getLogger(LtiProxyConfigService.class);

    @Autowired
    LtiProxyConfigRepository repository;

    /**
     * Allows convenient access to the DAO repositories which manage the stored LTI data
     * @return the repositories access service
     */
    public LtiProxyConfigRepository getRepository() {
        return this.repository;
    }

    /**
     * Loads up the data which is referenced in this LTI request (assuming it can be found in the DB)
     *
     * @param lti the LTIRequest which we are populating
     * @return true if any data was loaded OR false if none could be loaded (because no matching data was found or the input keys are not set)
     */
    @Transactional
    public void save(final LtiProxyConfig config) {
        assert config != null;
        this.repository.save(config);
    }

    public LtiProxyConfig find(final LtiProxyConfig config) {
        assert config != null;
        assert config.getUserId() != null : "User ID not populated in the LtiProxyConfig";
        assert config.getContextId() != null : "Context ID not populated in the LtiProxyConfig";
        return this.repository.findByUserIdAndContextIdAllIgnoringCase(config.getUserId(), config.getContextId());
    }

}