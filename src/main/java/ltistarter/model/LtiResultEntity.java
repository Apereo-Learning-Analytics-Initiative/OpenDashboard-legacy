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
import java.sql.Timestamp;

@Entity
@Table(name = "lti_result")
public class LtiResultEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "result_id", nullable = false, insertable = true, updatable = true)
    private long resultId;
    @Basic
    @Column(name = "sourcedid", nullable = false, insertable = true, updatable = true, length = 4096)
    private String sourcedid;
    @Basic
    @Column(name = "sourcedid_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    private String sourcedidSha256;
    @Basic
    @Column(name = "grade", nullable = true, insertable = true, updatable = true, precision = 0)
    private Float grade;
    @Basic
    @Column(name = "note", nullable = true, insertable = true, updatable = true, length = 4096)
    private String note;
    @Basic
    @Column(name = "server_grade", nullable = true, insertable = true, updatable = true, precision = 0)
    private Float serverGrade;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;
    @Basic
    @Column(name = "retrieved_at", nullable = false, insertable = true, updatable = true)
    private Timestamp retrievedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LtiLinkEntity link;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LtiUserEntity user;
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.DETACH)
    private LtiServiceEntity service;

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public String getSourcedid() {
        return sourcedid;
    }

    public void setSourcedid(String sourcedid) {
        this.sourcedid = sourcedid;
    }

    public String getSourcedidSha256() {
        return sourcedidSha256;
    }

    public void setSourcedidSha256(String sourcedidSha256) {
        this.sourcedidSha256 = sourcedidSha256;
    }

    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Float getServerGrade() {
        return serverGrade;
    }

    public void setServerGrade(Float serverGrade) {
        this.serverGrade = serverGrade;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Timestamp getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(Timestamp retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    public LtiLinkEntity getLink() {
        return link;
    }

    public void setLink(LtiLinkEntity link) {
        this.link = link;
    }

    public LtiUserEntity getUser() {
        return user;
    }

    public void setUser(LtiUserEntity user) {
        this.user = user;
    }

    public LtiServiceEntity getService() {
        return service;
    }

    public void setService(LtiServiceEntity service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiResultEntity that = (LtiResultEntity) o;

        if (resultId != that.resultId) return false;
        if (sourcedid != null ? !sourcedid.equals(that.sourcedid) : that.sourcedid != null) return false;
        if (sourcedidSha256 != null ? !sourcedidSha256.equals(that.sourcedidSha256) : that.sourcedidSha256 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) resultId;
        result = 31 * result + (sourcedid != null ? sourcedid.hashCode() : 0);
        result = 31 * result + (sourcedidSha256 != null ? sourcedidSha256.hashCode() : 0);
        return result;
    }

}
