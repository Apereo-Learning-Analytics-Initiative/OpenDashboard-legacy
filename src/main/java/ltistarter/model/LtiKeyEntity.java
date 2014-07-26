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
@Table(name = "lti_key")
public class LtiKeyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", nullable = false)
    private long keyId;
    @Basic
    @Column(name = "key_sha256", unique = true, nullable = false, insertable = true, updatable = true, length = 64)
    private String keySha256;
    @Basic
    @Column(name = "key_key", unique = true, nullable = false, insertable = true, updatable = true, length = 4096)
    private String keyKey;
    @Basic
    @Column(name = "secret", nullable = false, insertable = true, updatable = true, length = 4096)
    private String secret;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;
    @Basic
    @Column(nullable = true, length = 8192)
    private String settings;

    @OneToMany(mappedBy = "ltiKey", fetch = FetchType.LAZY)
    private Set<LtiContextEntity> contexts;
    @OneToMany(mappedBy = "ltiKey", fetch = FetchType.LAZY)
    private Set<LtiServiceEntity> services;

    protected LtiKeyEntity() {
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public Set<LtiContextEntity> getContexts() {
        return contexts;
    }

    public void setContexts(Set<LtiContextEntity> contexts) {
        this.contexts = contexts;
    }

    public Set<LtiServiceEntity> getServices() {
        return services;
    }

    public void setServices(Set<LtiServiceEntity> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiKeyEntity that = (LtiKeyEntity) o;

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
