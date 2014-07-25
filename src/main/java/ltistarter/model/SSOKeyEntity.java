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

@Entity
@Table(name = "sso_key")
public class SSOKeyEntity extends BaseEntity {
    public static String SOURCE_FACEBOOK = "Facebook";
    public static String SOURCE_GOOGLE = "Google";
    public static String SOURCE_LINKEDIN = "LinkedIn";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", nullable = false, insertable = true, updatable = true)
    private long keyId;
    @Basic
    @Column(name = "key_sha256", unique = true, nullable = false, insertable = true, updatable = true, length = 64)
    private String keySha256;
    @Basic
    @Column(name = "key_key", unique = true, nullable = false, insertable = true, updatable = true, length = 4096)
    private String keyKey;
    @Basic
    @Column(name = "source", nullable = false, insertable = true, updatable = true, length = 255)
    private String source;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private ProfileEntity profile;

    protected SSOKeyEntity() {
    }

    /**
     * @param key    the SSO key (from google, facebook, linkedin, etc.)
     * @param source the source of this key (google, facebook, linkedin, etc.)
     */
    public SSOKeyEntity(String key, String source) {
        assert StringUtils.isNotBlank(key);
        this.keyKey = key;
        this.keySha256 = makeSHA256(key);
        this.source = source;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getKeySha256() {
        return keySha256;
    }

    public void setKeySha256(String keySha256) {
        this.keySha256 = keySha256;
    }

    public String getKeyKey() {
        return keyKey;
    }

    public void setKeyKey(String keyKey) {
        this.keyKey = keyKey;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SSOKeyEntity that = (SSOKeyEntity) o;

        if (keyId != that.keyId) return false;
        if (keyKey != null ? !keyKey.equals(that.keyKey) : that.keyKey != null) return false;
        if (keySha256 != null ? !keySha256.equals(that.keySha256) : that.keySha256 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) keyId;
        result = 31 * result + (keySha256 != null ? keySha256.hashCode() : 0);
        result = 31 * result + (keyKey != null ? keyKey.hashCode() : 0);
        return result;
    }

}
