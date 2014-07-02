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
    private int membershipId;
    private int contextId;
    private int userId;
    private Short role;
    private Short roleOverride;
    private LtiContextEntity ltiContextByContextId;
    private LtiUserEntity ltiUserByUserId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "membership_id", nullable = false, insertable = true, updatable = true)
    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    @Basic
    @Column(name = "context_id", nullable = false, insertable = true, updatable = true)
    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
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
    @Column(name = "role", nullable = true, insertable = true, updatable = true)
    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    @Basic
    @Column(name = "role_override", nullable = true, insertable = true, updatable = true)
    public Short getRoleOverride() {
        return roleOverride;
    }

    public void setRoleOverride(Short roleOverride) {
        this.roleOverride = roleOverride;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LtiMembershipEntity that = (LtiMembershipEntity) o;

        if (contextId != that.contextId) return false;
        if (membershipId != that.membershipId) return false;
        if (userId != that.userId) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (roleOverride != null ? !roleOverride.equals(that.roleOverride) : that.roleOverride != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = membershipId;
        result = 31 * result + contextId;
        result = 31 * result + userId;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (roleOverride != null ? roleOverride.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "context_id", referencedColumnName = "context_id", nullable = false, insertable = false, updatable = false)
    public LtiContextEntity getLtiContextByContextId() {
        return ltiContextByContextId;
    }

    public void setLtiContextByContextId(LtiContextEntity ltiContextByContextId) {
        this.ltiContextByContextId = ltiContextByContextId;
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
