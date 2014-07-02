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
@Table(name = "lti_link")
public class LtiLinkEntity extends BaseEntity {
    private int linkId;
    private String linkSha256;
    private String linkKey;
    private int contextId;
    private String title;
    private String json;

    private LtiContextEntity ltiContextByContextId;
    private Collection<LtiResultEntity> ltiResultsByLinkId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "link_id", nullable = false, insertable = true, updatable = true)
    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    @Basic
    @Column(name = "link_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    public String getLinkSha256() {
        return linkSha256;
    }

    public void setLinkSha256(String linkSha256) {
        this.linkSha256 = linkSha256;
    }

    @Basic
    @Column(name = "link_key", nullable = false, insertable = true, updatable = true, length = 4096)
    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    @Basic
    @Column(name = "context_id", nullable = false, insertable = true, updatable = true)
    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
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

        LtiLinkEntity that = (LtiLinkEntity) o;

        if (contextId != that.contextId) return false;
        if (linkId != that.linkId) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (linkKey != null ? !linkKey.equals(that.linkKey) : that.linkKey != null) return false;
        if (linkSha256 != null ? !linkSha256.equals(that.linkSha256) : that.linkSha256 != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = linkId;
        result = 31 * result + (linkSha256 != null ? linkSha256.hashCode() : 0);
        result = 31 * result + (linkKey != null ? linkKey.hashCode() : 0);
        result = 31 * result + contextId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "context_id", referencedColumnName = "context_id", nullable = false, insertable = false, updatable = false)
    public LtiContextEntity getLtiContextByContextId() {
        return ltiContextByContextId;
    }

    public void setLtiContextByContextId(LtiContextEntity ltiContextByContextId) {
        this.ltiContextByContextId = ltiContextByContextId;
    }

    @OneToMany(mappedBy = "ltiLinkByLinkId")
    public Collection<LtiResultEntity> getLtiResultsByLinkId() {
        return ltiResultsByLinkId;
    }

    public void setLtiResultsByLinkId(Collection<LtiResultEntity> ltiResultsByLinkId) {
        this.ltiResultsByLinkId = ltiResultsByLinkId;
    }

}
