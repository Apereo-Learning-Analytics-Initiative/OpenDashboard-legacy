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

import ltistarter.model.LtiKeyEntity;
import ltistarter.repository.LtiKeyRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

/**
 * Sample consumer details service which verifies the key and secret using the LTI key DB.
 * Populates the ConsumerDetails.consumerName with the ID of the LtiKeyEntity if a match is found
 * and grants the OAUTH and LTI Authority Roles
 */
@Component
public class LTIConsumerDetailsService implements ConsumerDetailsService {

    final static Logger log = LoggerFactory.getLogger(LTIConsumerDetailsService.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    LtiKeyRepository ltiKeyRepository;

    @Override
    public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
        consumerKey = StringUtils.trimToNull(consumerKey);
        assert StringUtils.isNotEmpty(consumerKey) : "consumerKey must be set and not null";
        BaseConsumerDetails cd;
        LtiKeyEntity ltiKey = ltiKeyRepository.findByKeyKey(consumerKey);
        if (ltiKey == null) {
            // no matching key found
            throw new OAuthException("No matching lti key record was found for " + consumerKey);
        } else {
            cd = new BaseConsumerDetails();
            cd.setConsumerKey(consumerKey);
            cd.setSignatureSecret(new SharedConsumerSecretImpl(ltiKey.getSecret()));
            cd.setConsumerName(String.valueOf(ltiKey.getKeyId()));
            cd.setRequiredToObtainAuthenticatedToken(false); // no token required (0-legged)
            cd.getAuthorities().add(new SimpleGrantedAuthority("ROLE_OAUTH")); // add the ROLE_OAUTH (can add others as well)
            cd.getAuthorities().add(new SimpleGrantedAuthority("ROLE_LTI"));
            log.info("LTI check SUCCESS, consumer key: " + consumerKey);
        }
        return cd;
    }

}
