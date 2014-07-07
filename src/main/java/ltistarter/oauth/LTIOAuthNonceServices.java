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

import org.springframework.security.oauth.provider.nonce.InMemoryNonceServices;
import org.springframework.stereotype.Component;

@Component
public class LTIOAuthNonceServices extends InMemoryNonceServices {

    @Override
    public long getValidityWindowSeconds() {
        return 1200;
    }

}
