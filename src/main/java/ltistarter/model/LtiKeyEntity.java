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
import java.util.Collection;

@Entity
@Table(name = "lti_key")
public class LtiKeyEntity extends BaseEntity {
    private int keyId;
    private String keySha256;
    private String keyKey;
    private String secret;
    private Integer userId;
    private String json;

    private Collection<LtiContextEntity> ltiContextsByKeyId;
    private Collection<LtiServiceEntity> ltiServicesByKeyId;
    private Collection<LtiUserEntity> ltiUsersByKeyId;

    LtiKeyEntity() {
    }

    /**
     * @param key    the key
     * @param secret [OPTIONAL] secret (can be null)
     */
    public LtiKeyEntity(String key, String secret) {
        assert StringUtils.isNotBlank(key);
        this.keyKey = key;
        this.keySha256 = makeSHA256(key);
        if (StringUtils.isNotBlank(secret)) {
            this.secret = secret;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", nullable = false, insertable = true, updatable = true)
    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    @Basic
    @Column(name = "key_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    public String getKeySha256() {
        return keySha256;
    }

    public void setKeySha256(String keySha256) {
        this.keySha256 = keySha256;
    }

    @Basic
    @Column(name = "key_key", nullable = false, insertable = true, updatable = true, length = 4096)
    public String getKeyKey() {
        return keyKey;
    }

    public void setKeyKey(String keyKey) {
        this.keyKey = keyKey;
    }

    @Basic
    @Column(name = "secret", nullable = true, insertable = true, updatable = true, length = 4096)
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Basic
    @Column(name = "user_id", nullable = true, insertable = true, updatable = true)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

        LtiKeyEntity that = (LtiKeyEntity) o;

        if (keyId != that.keyId) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (keyKey != null ? !keyKey.equals(that.keyKey) : that.keyKey != null) return false;
        if (keySha256 != null ? !keySha256.equals(that.keySha256) : that.keySha256 != null) return false;
        if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyId;
        result = 31 * result + (keySha256 != null ? keySha256.hashCode() : 0);
        result = 31 * result + (keyKey != null ? keyKey.hashCode() : 0);
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "ltiKeyByKeyId")
    public Collection<LtiContextEntity> getLtiContextsByKeyId() {
        return ltiContextsByKeyId;
    }

    public void setLtiContextsByKeyId(Collection<LtiContextEntity> ltiContextsByKeyId) {
        this.ltiContextsByKeyId = ltiContextsByKeyId;
    }

    @OneToMany(mappedBy = "ltiKeyByKeyId")
    public Collection<LtiServiceEntity> getLtiServicesByKeyId() {
        return ltiServicesByKeyId;
    }

    public void setLtiServicesByKeyId(Collection<LtiServiceEntity> ltiServicesByKeyId) {
        this.ltiServicesByKeyId = ltiServicesByKeyId;
    }

    @OneToMany(mappedBy = "ltiKeyByKeyId")
    public Collection<LtiUserEntity> getLtiUsersByKeyId() {
        return ltiUsersByKeyId;
    }

    public void setLtiUsersByKeyId(Collection<LtiUserEntity> ltiUsersByKeyId) {
        this.ltiUsersByKeyId = ltiUsersByKeyId;
    }
}
