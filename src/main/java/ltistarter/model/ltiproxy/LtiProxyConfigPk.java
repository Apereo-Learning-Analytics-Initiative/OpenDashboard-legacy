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

package ltistarter.model.ltiproxy;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LtiProxyConfigPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String contextId;

    public LtiProxyConfigPk(
            final String userId,
            final String contextId) {
        this.userId = userId;
        this.contextId = contextId;
   }

    public LtiProxyConfigPk() {
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String id) {
        this.userId = id;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(final String id) {
        this.contextId = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
