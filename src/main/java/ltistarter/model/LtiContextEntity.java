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
package ltistarter.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "lti_context")
public class LtiContextEntity extends BaseEntity {
    private long contextId;
    private String contextSha256;
    private String contextKey;
    private int keyId;
    private String title;
    private String json;

    private LtiKeyEntity ltiKeyByKeyId;
    private Collection<LtiLinkEntity> ltiLinksByContextId;
    private Collection<LtiMembershipEntity> ltiMembershipsByContextId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "context_id", nullable = false, insertable = true, updatable = true)
    public long getContextId() {
        return contextId;
    }

    public void setContextId(long contextId) {
        this.contextId = contextId;
    }

    @Basic
    @Column(name = "context_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    public String getContextSha256() {
        return contextSha256;
    }

    public void setContextSha256(String contextSha256) {
        this.contextSha256 = contextSha256;
    }

    @Basic
    @Column(name = "context_key", nullable = false, insertable = true, updatable = true, length = 4096)
    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    @Basic
    @Column(name = "key_id", nullable = false, insertable = true, updatable = true)
    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    @Basic
    @Column(name = "title", nullable = true, insertable = true, updatable = true, length = 2048)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiContextEntity that = (LtiContextEntity) o;

        if (contextId != that.contextId) return false;
        if (keyId != that.keyId) return false;
        if (contextKey != null ? !contextKey.equals(that.contextKey) : that.contextKey != null) return false;
        if (contextSha256 != null ? !contextSha256.equals(that.contextSha256) : that.contextSha256 != null)
            return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) contextId;
        result = 31 * result + (contextSha256 != null ? contextSha256.hashCode() : 0);
        result = 31 * result + (contextKey != null ? contextKey.hashCode() : 0);
        result = 31 * result + keyId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "key_id", referencedColumnName = "key_id", nullable = false, insertable = false, updatable = false)
    public LtiKeyEntity getLtiKeyByKeyId() {
        return ltiKeyByKeyId;
    }

    public void setLtiKeyByKeyId(LtiKeyEntity ltiKeyByKeyId) {
        this.ltiKeyByKeyId = ltiKeyByKeyId;
    }

    @OneToMany(mappedBy = "ltiContextByContextId")
    public Collection<LtiLinkEntity> getLtiLinksByContextId() {
        return ltiLinksByContextId;
    }

    public void setLtiLinksByContextId(Collection<LtiLinkEntity> ltiLinksByContextId) {
        this.ltiLinksByContextId = ltiLinksByContextId;
    }

    @OneToMany(mappedBy = "ltiContextByContextId")
    public Collection<LtiMembershipEntity> getLtiMembershipsByContextId() {
        return ltiMembershipsByContextId;
    }

    public void setLtiMembershipsByContextId(Collection<LtiMembershipEntity> ltiMembershipsByContextId) {
        this.ltiMembershipsByContextId = ltiMembershipsByContextId;
    }

}
