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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profile")
public class ProfileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "profile_id", nullable = false, insertable = true, updatable = true)
    private long profileId;
    @Basic
    @Column(name = "profile_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    private String profileSha256;
    @Basic
    @Column(name = "profile_key", nullable = false, insertable = true, updatable = true, length = 4096)
    private String profileKey;
    @Basic
    @Column(name = "displayName", nullable = true, insertable = true, updatable = true, length = 2048)
    private String displayName;
    @Basic
    @Column(name = "email", nullable = true, insertable = true, updatable = true, length = 2048)
    private String email;
    @Basic
    @Column(name = "locale", nullable = true, insertable = true, updatable = true, length = 63)
    private String locale;
    @Basic
    @Column(name = "subscribe", nullable = true, insertable = true, updatable = true)
    private Short subscribe;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;
    @Basic
    @Column(name = "login_at", nullable = false, insertable = true, updatable = true)
    private Timestamp loginAt;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private Set<SSOKeyEntity> ssoKeys = new HashSet<>();
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private Set<LtiUserEntity> users = new HashSet<>();

    public ProfileEntity() {
    }

    public ProfileEntity(String profileKey, Date loginAt, String email) {
        assert profileKey != null;
        if (loginAt == null) {
            loginAt = new Date();
        }
        this.profileKey = profileKey;
        this.profileSha256 = makeSHA256(profileKey);
        this.loginAt = new Timestamp(loginAt.getTime());
        this.email = email;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getProfileSha256() {
        return profileSha256;
    }

    public void setProfileSha256(String profileSha256) {
        this.profileSha256 = profileSha256;
    }

    public String getProfileKey() {
        return profileKey;
    }

    public void setProfileKey(String profileKey) {
        this.profileKey = profileKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Short getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Short subscribe) {
        this.subscribe = subscribe;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Timestamp getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Timestamp loginAt) {
        this.loginAt = loginAt;
    }

    public Set<SSOKeyEntity> getSsoKeys() {
        return ssoKeys;
    }

    public void setSsoKeys(Set<SSOKeyEntity> ssoKeys) {
        this.ssoKeys = ssoKeys;
    }

    public Set<LtiUserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<LtiUserEntity> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfileEntity that = (ProfileEntity) o;

        if (profileId != that.profileId) return false;
        if (profileKey != null ? !profileKey.equals(that.profileKey) : that.profileKey != null) return false;
        if (profileSha256 != null ? !profileSha256.equals(that.profileSha256) : that.profileSha256 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) profileId;
        result = 31 * result + (profileSha256 != null ? profileSha256.hashCode() : 0);
        result = 31 * result + (profileKey != null ? profileKey.hashCode() : 0);
        return result;
    }

}
