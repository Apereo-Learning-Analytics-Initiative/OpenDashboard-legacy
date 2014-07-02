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
@Table(name = "lti_service")
public class LtiServiceEntity extends BaseEntity {
    private long serviceId;
    private String serviceSha256;
    private String serviceKey;
    private long keyId;
    private String format;
    private String json;

    private Collection<LtiResultEntity> ltiResultsByServiceId;
    private LtiKeyEntity ltiKeyByKeyId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "service_id", nullable = false, insertable = true, updatable = true)
    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "service_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    public String getServiceSha256() {
        return serviceSha256;
    }

    public void setServiceSha256(String serviceSha256) {
        this.serviceSha256 = serviceSha256;
    }

    @Basic
    @Column(name = "service_key", nullable = false, insertable = true, updatable = true, length = 4096)
    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    @Basic
    @Column(name = "key_id", nullable = false, insertable = true, updatable = true)
    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    @Basic
    @Column(name = "format", nullable = true, insertable = true, updatable = true, length = 1024)
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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

        LtiServiceEntity that = (LtiServiceEntity) o;

        if (keyId != that.keyId) return false;
        if (serviceId != that.serviceId) return false;
        if (format != null ? !format.equals(that.format) : that.format != null) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (serviceKey != null ? !serviceKey.equals(that.serviceKey) : that.serviceKey != null) return false;
        if (serviceSha256 != null ? !serviceSha256.equals(that.serviceSha256) : that.serviceSha256 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) serviceId;
        result = 31 * result + (serviceSha256 != null ? serviceSha256.hashCode() : 0);
        result = 31 * result + (serviceKey != null ? serviceKey.hashCode() : 0);
        result = 31 * result + (int) keyId;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "ltiServiceByServiceId")
    public Collection<LtiResultEntity> getLtiResultsByServiceId() {
        return ltiResultsByServiceId;
    }

    public void setLtiResultsByServiceId(Collection<LtiResultEntity> ltiResultsByServiceId) {
        this.ltiResultsByServiceId = ltiResultsByServiceId;
    }

    @ManyToOne
    @JoinColumn(name = "key_id", referencedColumnName = "key_id", nullable = false, insertable = false, updatable = false)
    public LtiKeyEntity getLtiKeyByKeyId() {
        return ltiKeyByKeyId;
    }

    public void setLtiKeyByKeyId(LtiKeyEntity ltiKeyByKeyId) {
        this.ltiKeyByKeyId = ltiKeyByKeyId;
    }
}
