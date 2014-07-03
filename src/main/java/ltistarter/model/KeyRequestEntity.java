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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id", nullable = false, insertable = true, updatable = true)
    private long requestId;
    @Basic
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 4096)
    private String title;
    @Basic
    @Column(name = "notes", nullable = true, insertable = true, updatable = true, length = 65535)
    private String notes;
    @Basic
    @Column(name = "admin", nullable = true, insertable = true, updatable = true, length = 65535)
    private String admin;
    @Basic
    @Column(name = "state", nullable = true, insertable = true, updatable = true)
    private Short state;
    @Basic
    @Column(name = "lti", nullable = true, insertable = true, updatable = true)
    private Byte lti;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private LtiUserEntity user;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public Byte getLti() {
        return lti;
    }

    public void setLti(Byte lti) {
        this.lti = lti;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public LtiUserEntity getUser() {
        return user;
    }

    public void setUser(LtiUserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyRequestEntity that = (KeyRequestEntity) o;

        if (requestId != that.requestId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) requestId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

}
