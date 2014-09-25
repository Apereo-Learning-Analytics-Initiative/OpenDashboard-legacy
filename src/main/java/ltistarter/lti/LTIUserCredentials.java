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

import java.security.Principal;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.oauth.provider.ConsumerCredentials;

public class LTIUserCredentials extends ConsumerCredentials implements Principal {
    private static final long serialVersionUID = 1L;

    protected String contextId;
    protected String userId;
    protected Map<String,String> oauthParameters;

    public LTIUserCredentials(
            final String userId,
            final String contextId,
            final String consumerKey,
            final String signature,
            final String signatureMethod,
            final String signatureBaseString,
            final String token,
            final Map<String,String> oauthParameters) {
        super(consumerKey, signature, signatureMethod, signatureBaseString, token);
        assert contextId != null;
        assert userId != null;
        this.contextId = contextId;
        this.userId = userId;
        this.oauthParameters = oauthParameters;
    }

    public String getContextId() {
        return this.contextId;
    }
    public String getUserId() {
        return this.userId;
    }
    public Map<String,String>getOauthParameters() {
        return this.oauthParameters;
    }

    @Override
    public String getName() {
        return this.getConsumerKey() + ":" + this.userId + ":" + this.contextId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
