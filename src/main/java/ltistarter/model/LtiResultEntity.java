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
    private int resultId;
    private int linkId;
    private int userId;
    private String sourcedid;
    private String sourcedidSha256;
    private Integer serviceId;
    private Float grade;
    private String note;
    private Float serverGrade;
    private String json;
    private Timestamp retrievedAt;

    private LtiLinkEntity ltiLinkByLinkId;
    private LtiUserEntity ltiUserByUserId;
    private LtiServiceEntity ltiServiceByServiceId;

    @Id
    @Column(name = "result_id", nullable = false, insertable = true, updatable = true)
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    @Basic
    @Column(name = "link_id", nullable = false, insertable = true, updatable = true)
    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    @Basic
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "sourcedid", nullable = false, insertable = true, updatable = true, length = 2048)
    public String getSourcedid() {
        return sourcedid;
    }

    public void setSourcedid(String sourcedid) {
        this.sourcedid = sourcedid;
    }

    @Basic
    @Column(name = "sourcedid_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    public String getSourcedidSha256() {
        return sourcedidSha256;
    }

    public void setSourcedidSha256(String sourcedidSha256) {
        this.sourcedidSha256 = sourcedidSha256;
    }

    @Basic
    @Column(name = "service_id", nullable = true, insertable = true, updatable = true)
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "grade", nullable = true, insertable = true, updatable = true, precision = 0)
    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    @Basic
    @Column(name = "note", nullable = true, insertable = true, updatable = true, length = 2048)
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Basic
    @Column(name = "server_grade", nullable = true, insertable = true, updatable = true, precision = 0)
    public Float getServerGrade() {
        return serverGrade;
    }

    public void setServerGrade(Float serverGrade) {
        this.serverGrade = serverGrade;
    }

    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Basic
    @Column(name = "retrieved_at", nullable = false, insertable = true, updatable = true)
    public Timestamp getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(Timestamp retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiResultEntity that = (LtiResultEntity) o;

        if (linkId != that.linkId) return false;
        if (resultId != that.resultId) return false;
        if (userId != that.userId) return false;
        if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (retrievedAt != null ? !retrievedAt.equals(that.retrievedAt) : that.retrievedAt != null) return false;
        if (serverGrade != null ? !serverGrade.equals(that.serverGrade) : that.serverGrade != null) return false;
        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        if (sourcedid != null ? !sourcedid.equals(that.sourcedid) : that.sourcedid != null) return false;
        if (sourcedidSha256 != null ? !sourcedidSha256.equals(that.sourcedidSha256) : that.sourcedidSha256 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resultId;
        result = 31 * result + linkId;
        result = 31 * result + userId;
        result = 31 * result + (sourcedid != null ? sourcedid.hashCode() : 0);
        result = 31 * result + (sourcedidSha256 != null ? sourcedidSha256.hashCode() : 0);
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (serverGrade != null ? serverGrade.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        result = 31 * result + (retrievedAt != null ? retrievedAt.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "link_id", referencedColumnName = "link_id", nullable = false, insertable = false, updatable = false)
    public LtiLinkEntity getLtiLinkByLinkId() {
        return ltiLinkByLinkId;
    }

    public void setLtiLinkByLinkId(LtiLinkEntity ltiLinkByLinkId) {
        this.ltiLinkByLinkId = ltiLinkByLinkId;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    public LtiUserEntity getLtiUserByUserId() {
        return ltiUserByUserId;
    }

    public void setLtiUserByUserId(LtiUserEntity ltiUserByUserId) {
        this.ltiUserByUserId = ltiUserByUserId;
    }

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", insertable = false, updatable = false)
    public LtiServiceEntity getLtiServiceByServiceId() {
        return ltiServiceByServiceId;
    }

    public void setLtiServiceByServiceId(LtiServiceEntity ltiServiceByServiceId) {
        this.ltiServiceByServiceId = ltiServiceByServiceId;
    }
}
