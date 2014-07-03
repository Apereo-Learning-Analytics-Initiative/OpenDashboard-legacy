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
@Table(name = "lti_membership")
public class LtiMembershipEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "membership_id", nullable = false, insertable = true, updatable = true)
    private long membershipId;
    @Basic
    @Column(name = "role", nullable = true, insertable = true, updatable = true)
    private Short role;
    @Basic
    @Column(name = "role_override", nullable = true, insertable = true, updatable = true)
    private Short roleOverride;
    @ManyToOne
    @JoinColumn(name = "context_id", referencedColumnName = "context_id", nullable = false, insertable = false, updatable = false)
    private LtiContextEntity context;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private LtiUserEntity user;

    public long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(long membershipId) {
        this.membershipId = membershipId;
    }

    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    public Short getRoleOverride() {
        return roleOverride;
    }

    public void setRoleOverride(Short roleOverride) {
        this.roleOverride = roleOverride;
    }

    public LtiContextEntity getContext() {
        return context;
    }

    public void setContext(LtiContextEntity context) {
        this.context = context;
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

        LtiMembershipEntity that = (LtiMembershipEntity) o;

        if (context.getContextId() != that.context.getContextId()) return false;
        if (membershipId != that.membershipId) return false;
        if (user.getUserId() != that.user.getUserId()) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) membershipId;
        result = 31 * result + (int) context.getContextId();
        result = 31 * result + (int) user.getUserId();
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

}
