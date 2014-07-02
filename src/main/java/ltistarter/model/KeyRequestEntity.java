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

@Entity
@Table(name = "key_request")
public class KeyRequestEntity extends BaseEntity {
    private long requestId;
    private long userId;
    private String title;
    private String notes;
    private String admin;
    private Short state;
    private Byte lti;
    private String json;

    private LtiUserEntity ltiUserByUserId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id", nullable = false, insertable = true, updatable = true)
    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Basic
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 512)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "notes", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Basic
    @Column(name = "admin", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Basic
    @Column(name = "state", nullable = true, insertable = true, updatable = true)
    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    @Basic
    @Column(name = "lti", nullable = true, insertable = true, updatable = true)
    public Byte getLti() {
        return lti;
    }

    public void setLti(Byte lti) {
        this.lti = lti;
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

        KeyRequestEntity that = (KeyRequestEntity) o;

        if (requestId != that.requestId) return false;
        if (userId != that.userId) return false;
        if (admin != null ? !admin.equals(that.admin) : that.admin != null) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (lti != null ? !lti.equals(that.lti) : that.lti != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) requestId;
        result = 31 * result + (int) userId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (lti != null ? lti.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    public LtiUserEntity getLtiUserByUserId() {
        return ltiUserByUserId;
    }

    public void setLtiUserByUserId(LtiUserEntity ltiUserByUserId) {
        this.ltiUserByUserId = ltiUserByUserId;
    }
}
