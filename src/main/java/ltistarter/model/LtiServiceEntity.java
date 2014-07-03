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
import java.util.Set;

@Entity
@Table(name = "lti_service")
public class LtiServiceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "service_id", nullable = false, insertable = true, updatable = true)
    private long serviceId;
    @Basic
    @Column(name = "service_sha256", nullable = false, insertable = true, updatable = true, length = 64)
    private String serviceSha256;
    @Basic
    @Column(name = "service_key", nullable = false, insertable = true, updatable = true, length = 4096)
    private String serviceKey;
    @Basic
    @Column(name = "format", nullable = true, insertable = true, updatable = true, length = 1024)
    private String format;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;

    @OneToMany(mappedBy = "resultId")
    private Set<LtiResultEntity> results;
    @ManyToOne
    @JoinColumn(name = "key_id", referencedColumnName = "key_id", nullable = false, insertable = false, updatable = false)
    private LtiKeyEntity ltiKey;

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceSha256() {
        return serviceSha256;
    }

    public void setServiceSha256(String serviceSha256) {
        this.serviceSha256 = serviceSha256;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Set<LtiResultEntity> getResults() {
        return results;
    }

    public void setResults(Set<LtiResultEntity> results) {
        this.results = results;
    }

    public LtiKeyEntity getLtiKey() {
        return ltiKey;
    }

    public void setLtiKey(LtiKeyEntity ltiKey) {
        this.ltiKey = ltiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiServiceEntity that = (LtiServiceEntity) o;

        if (serviceId != that.serviceId) return false;
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
        return result;
    }

}
