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

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "lti_link")
public class LtiLinkEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "link_id", nullable = false, insertable = true, updatable = true)
    private long linkId;
    @Basic
    @Column(name = "link_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    private String linkSha256;
    @Basic
    @Column(name = "link_key", nullable = false, insertable = true, updatable = true, length = 4096)
    private String linkKey;
    @Basic
    @Column(name = "title", nullable = true, insertable = true, updatable = true, length = 4096)
    private String title;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "context_id")
    private LtiContextEntity context;
    @OneToMany(mappedBy = "link")
    private Set<LtiResultEntity> results;

    protected LtiLinkEntity() {
    }

    /**
     * @param linkKey the external id for this link
     * @param context the LTI context
     * @param title   OPTIONAL title of this link (null for none)
     */
    public LtiLinkEntity(String linkKey, LtiContextEntity context, String title) {
        assert StringUtils.isNotBlank(linkKey);
        assert context != null;
        this.linkKey = linkKey;
        this.linkSha256 = makeSHA256(linkKey);
        this.context = context;
        this.title = title;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public String getLinkSha256() {
        return linkSha256;
    }

    public void setLinkSha256(String linkSha256) {
        this.linkSha256 = linkSha256;
    }

    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public LtiContextEntity getContext() {
        return context;
    }

    public void setContext(LtiContextEntity context) {
        this.context = context;
    }

    public Set<LtiResultEntity> getResults() {
        return results;
    }

    public void setResults(Set<LtiResultEntity> results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiLinkEntity that = (LtiLinkEntity) o;

        if (linkId != that.linkId) return false;
        if (linkKey != null ? !linkKey.equals(that.linkKey) : that.linkKey != null) return false;
        if (linkSha256 != null ? !linkSha256.equals(that.linkSha256) : that.linkSha256 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) linkId;
        result = 31 * result + (linkSha256 != null ? linkSha256.hashCode() : 0);
        result = 31 * result + (linkKey != null ? linkKey.hashCode() : 0);
        return result;
    }

}
