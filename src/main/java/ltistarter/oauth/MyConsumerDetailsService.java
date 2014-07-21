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
package ltistarter.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

/**
 * Sample consumer details service which verifies the key and secret,
 * In our case the key is "key" and secret is "secret"
 */
@Component
public class MyConsumerDetailsService implements ConsumerDetailsService {

    final static Logger log = LoggerFactory.getLogger(MyConsumerDetailsService.class);

    @Override
    public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
        BaseConsumerDetails cd;
        // NOTE: really lookup the key and secret, for the sample here we just hardcoded
        if ("key".equals(consumerKey)) {
            // allow this oauth request
            cd = new BaseConsumerDetails();
            cd.setConsumerKey(consumerKey);
            cd.setSignatureSecret(new SharedConsumerSecretImpl("secret"));
            cd.setConsumerName("Sample");
            cd.setRequiredToObtainAuthenticatedToken(false); // no token required (0-legged)
            cd.getAuthorities().add(new SimpleGrantedAuthority("ROLE_OAUTH")); // add the ROLE_OAUTH (can add others as well)
            log.info("OAuth check SUCCESS, consumer key: " + consumerKey);
        } else {
            // deny - failed to match
            throw new OAuthException("For this example, key must be 'key'");
        }
        return cd;
    }

}
