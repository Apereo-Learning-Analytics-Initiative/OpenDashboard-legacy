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
package ltistarter.lti;

import ltistarter.model.*;
import ltistarter.repository.AllRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * This manages all the data processing for the LTIRequest (and for LTI in general)
 * Necessary to get appropriate TX handling and service management
 */
@Component
public class LTIDataService {

    final static Logger log = LoggerFactory.getLogger(LTIDataService.class);

    @Autowired
    AllRepositories repos;

    /**
     * Allows convenient access to the DAO repositories which manage the stored LTI data
     * @return the repositories access service
     */
    public AllRepositories getRepos() {
        return repos;
    }

    /**
     * Loads up the data which is referenced in this LTI request (assuming it can be found in the DB)
     *
     * @param lti the LTIRequest which we are populating
     * @return true if any data was loaded OR false if none could be loaded (because no matching data was found or the input keys are not set)
     */
    @Transactional
    public boolean loadLTIDataFromDB(LTIRequest lti) {
        assert repos != null;
        lti.loaded = false;
        if (lti.ltiConsumerKey == null) {
            // don't even attempt this without a key, it's pointless
            log.info("LTIload: No key to load lti.results for");
            return false;
        }
        boolean includesService = (lti.ltiServiceId != null);
        boolean includesSourcedid = (lti.ltiSourcedid != null);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT k, c, l, m, u"); //k.keyId, k.keyKey, k.secret, c.contextId, c.title AS contextTitle, l.linkId, l.title AS linkTitle, u.userId, u.displayName AS userDisplayName, u.email AS userEmail, u.subscribe, u.userSha256, m.membershipId, m.role, m.roleOverride"); // 15

        if (includesService) {
            sb.append(", s"); //, s.serviceId, s.serviceKey AS service"); // 2
        }
        if (includesSourcedid) {
            sb.append(", r"); //, r.lti.resultId, r.sourcedid, r.grade"); // 3
        }
        /*
        if (includeProfile) {
            sb.append(", p"); //", p.profileId, p.displayName AS profileDisplayName, p.email AS profileEmail, p.subscribe AS profileSubscribe"); // 4
        }*/

        sb.append(" FROM LtiKeyEntity k " +
                "LEFT JOIN k.contexts c ON c.contextSha256 = :context " + // LtiContextEntity
                "LEFT JOIN c.links l ON l.linkSha256 = :link " + // LtiLinkEntity
                "LEFT JOIN c.memberships m " + // LtiMembershipEntity
                "LEFT JOIN m.user u ON u.userSha256 = :user " // LtiUserEntity
        );

        if (includesService) {
            sb.append(" LEFT JOIN k.services s ON s.serviceSha256 = :service"); // LtiServiceEntity
        }
        if (includesSourcedid) {
            sb.append(" LEFT JOIN u.results r ON r.sourcedidSha256 = :sourcedid"); // LtiResultEntity
        }
        /*
        if (includeProfile) {
            sb.append(" LEFT JOIN u.profile p"); // ProfileEntity
        }*/
        sb.append(" WHERE k.keySha256 = :key AND (m IS NULL OR (m.context = c AND m.user = u))");
        /*
        if (includeProfile) {
            sb.append(" AND (u IS NULL OR u.profile = p)");
        }*/

        String sql = sb.toString();
        Query q = repos.entityManager.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("key", BaseEntity.makeSHA256(lti.ltiConsumerKey));
        q.setParameter("context", BaseEntity.makeSHA256(lti.ltiContextId));
        q.setParameter("link", BaseEntity.makeSHA256(lti.ltiLinkId));
        q.setParameter("user", BaseEntity.makeSHA256(lti.ltiUserId));
        if (includesService) {
            q.setParameter("service", BaseEntity.makeSHA256(lti.ltiServiceId));
        }
        if (includesSourcedid) {
            q.setParameter("sourcedid", BaseEntity.makeSHA256(lti.ltiSourcedid));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        if (rows == null || rows.isEmpty()) {
            log.info("LTIload: No lti.results found for key=" + lti.ltiConsumerKey);
        } else {
            // k, c, l, m, u, s, r
            Object[] row = rows.get(0);
            if (row.length > 0) lti.key = (LtiKeyEntity) row[0];
            if (row.length > 1) lti.context = (LtiContextEntity) row[1];
            if (row.length > 2) lti.link = (LtiLinkEntity) row[2];
            if (row.length > 3) lti.membership = (LtiMembershipEntity) row[3];
            if (row.length > 4) lti.user = (LtiUserEntity) row[4];
            if (includesService && includesSourcedid) {
                if (row.length > 5) lti.service = (LtiServiceEntity) row[5];
                if (row.length > 6) lti.result = (LtiResultEntity) row[6];
            } else if (includesService) {
                if (row.length > 5) lti.service = (LtiServiceEntity) row[5];
            } else if (includesSourcedid) {
                if (row.length > 5) lti.result = (LtiResultEntity) row[5];
            }

            // handle SPECIAL post lookup processing
            // If there is an appropriate role override variable, we use that role
            if (lti.membership != null && lti.membership.getRoleOverride() != null) {
                int roleOverrideNum = lti.membership.getRoleOverride();
                if (roleOverrideNum > lti.userRoleNumber) {
                    lti.userRoleNumber = roleOverrideNum;
                }
            }

            // check if the loading lti.resulted in a complete set of LTI data
            lti.checkCompleteLTIRequest(true);
            lti.loaded = true;
            log.info("LTIload: loaded data for key=" + lti.ltiConsumerKey + " and context=" + lti.ltiContextId + ", complete=" + lti.complete);
        }
        return lti.loaded;
    }

    /**
     * Attempts to insert or update the various LTI launch data (or other data that is part of this request)
     *
     * @param lti the LTIRequest which we are populating
     * @return the number of changes (inserts or updates) that occur
     */
    @Transactional
    public int updateLTIDataInDB(LTIRequest lti) {
        assert repos != null : "access to the repos is required";
        assert lti.loaded : "Data must be loaded before it can be updated";

        assert lti.key != null : "Key data must not be null to update data";
        repos.entityManager.merge(lti.key); // reconnect the key object for this transaction

        int inserts = 0;
        int updates = 0;
        if (lti.context == null && lti.ltiContextId != null) {
            LtiContextEntity newContext = new LtiContextEntity(lti.ltiContextId, lti.key, lti.ltiContextTitle, null);
            lti.context = repos.contexts.save(newContext);
            inserts++;
            log.info("LTIupdate: Inserted context id=" + lti.ltiContextId);
        } else if (lti.context != null) {
            repos.entityManager.merge(lti.context); // reconnect object for this transaction
            lti.ltiContextId = lti.context.getContextKey();
            log.info("LTIupdate: Reconnected existing context id=" + lti.ltiContextId);
        }

        if (lti.link == null && lti.ltiLinkId != null) {
            LtiLinkEntity newLink = new LtiLinkEntity(lti.ltiLinkId, lti.context, lti.ltiLinkTitle);
            lti.link = repos.links.save(newLink);
            inserts++;
            log.info("LTIupdate: Inserted link id=" + lti.ltiLinkId);
        } else if (lti.link != null) {
            repos.entityManager.merge(lti.link); // reconnect object for this transaction
            lti.ltiLinkId = lti.link.getLinkKey();
            log.info("LTIupdate: Reconnected existing link id=" + lti.ltiLinkId);
        }

        if (lti.user == null && lti.ltiUserId != null) {
            LtiUserEntity newUser = new LtiUserEntity(lti.ltiUserId, null);
            newUser.setDisplayName(lti.ltiUserDisplayName);
            newUser.setEmail(lti.ltiUserEmail);
            lti.user = repos.users.save(newUser);
            inserts++;
            log.info("LTIupdate: Inserted user id=" + lti.ltiUserId);
        } else if (lti.user != null) {
            repos.entityManager.merge(lti.user); // reconnect object for this transaction
            lti.ltiUserId = lti.user.getUserKey();
            lti.ltiUserDisplayName = lti.user.getDisplayName();
            lti.ltiUserEmail = lti.user.getEmail();
            log.info("LTIupdate: Reconnected existing user id=" + lti.ltiUserId);
        }

        if (lti.membership == null && lti.context != null && lti.user != null) {
            int roleNum = LTIRequest.makeUserRoleNum(lti.rawUserRoles); // NOTE: do not use userRoleNumber here, it may have been overridden
            LtiMembershipEntity newMember = new LtiMembershipEntity(lti.context, lti.user, roleNum);
            lti.membership = repos.members.save(newMember);
            inserts++;
            log.info("LTIupdate: Inserted membership id=" + newMember.getMembershipId() + ", role=" + newMember.getRole() + ", user=" + lti.ltiUserId + ", context=" + lti.ltiContextId);
        } else if (lti.membership != null) {
            repos.entityManager.merge(lti.membership); // reconnect object for this transaction
            lti.ltiUserId = lti.user.getUserKey();
            lti.ltiContextId = lti.context.getContextKey();
            log.info("LTIupdate: Reconnected existing membership id=" + lti.membership.getMembershipId());
        }

        // We need to handle the case where the service URL changes but we already have a sourcedid
        boolean serviceCreated = false;
        if (lti.service == null && lti.ltiServiceId != null && lti.ltiSourcedid != null) {
            LtiServiceEntity newService = new LtiServiceEntity(lti.ltiServiceId, lti.key, null);
            lti.service = repos.services.save(newService);
            inserts++;
            serviceCreated = true;
            log.info("LTIupdate: Inserted service id=" + lti.ltiServiceId);
        } else if (lti.service != null) {
            repos.entityManager.merge(lti.service); // reconnect object for this transaction
            lti.ltiServiceId = lti.service.getServiceKey();
            log.info("LTIupdate: Reconnected existing service id=" + lti.ltiServiceId);
        }

        // If we just created a new service entry but we already had a result entry, update it
        if (serviceCreated && lti.result != null && lti.ltiServiceId != null && lti.ltiSourcedid != null) {
            repos.entityManager.merge(lti.result); // reconnect object for this transaction
            lti.result.setSourcedid(lti.ltiSourcedid);
            repos.results.save(lti.result);
            inserts++;
            log.info("LTIupdate: Updated existing lti.result id=" + lti.result.getResultId() + ", sourcedid=" + lti.ltiSourcedid);
        }

        // If we don't have a lti.result but do have a service - link them together
        if (lti.result == null
                && lti.service != null && lti.user != null && lti.link != null
                && lti.ltiServiceId != null && lti.ltiSourcedid != null) {
            LtiResultEntity newResult = new LtiResultEntity(lti.ltiSourcedid, lti.user, lti.link, null, null);
            lti.result = repos.results.save(newResult);
            inserts++;
            log.info("LTIupdate: Inserted lti.result id=" + lti.result.getResultId());
        } else if (lti.result != null) {
            repos.entityManager.merge(lti.result); // reconnect object for this transaction
            lti.ltiSourcedid = lti.result.getSourcedid();
        }

        // If we don't have a result and do not have a service - just store the lti.result (prep for LTI 2.0)
        if (lti.result == null && lti.service == null && lti.user != null && lti.link != null && lti.ltiSourcedid != null) {
            LtiResultEntity newResult = new LtiResultEntity(lti.ltiSourcedid, lti.user, lti.link, null, null);
            lti.result = repos.results.save(newResult);
            inserts++;
            log.info("LTIupdate: Inserted LTI 2 lti.result id=" + lti.result.getResultId() + ", sourcedid=" + lti.ltiSourcedid);
        }

        // Here we handle updates to sourcedid
        if (lti.result != null && lti.ltiSourcedid != null && !lti.ltiSourcedid.equals(lti.result.getSourcedid())) {
            lti.result.setSourcedid(lti.ltiSourcedid);
            lti.result = repos.results.save(lti.result);
            updates++;
            log.info("LTIupdate: Updated lti.result (id=" + lti.result.getResultId() + ") sourcedid=" + lti.ltiSourcedid);
        }

        // Next we handle updates to context_title, link_title, user_displayname, user_email, or role

        if (lti.ltiContextTitle != null && lti.context != null && !lti.ltiContextTitle.equals(lti.context.getTitle())) {
            lti.context.setTitle(lti.ltiContextTitle);
            lti.context = repos.contexts.save(lti.context);
            updates++;
            log.info("LTIupdate: Updated context (id=" + lti.context.getContextId() + ") title=" + lti.ltiContextTitle);
        }

        if (lti.ltiLinkTitle != null && lti.link != null && !lti.ltiLinkTitle.equals(lti.link.getTitle())) {
            lti.link.setTitle(lti.ltiLinkTitle);
            lti.link = repos.links.save(lti.link);
            updates++;
            log.info("LTIupdate: Updated link (id=" + lti.link.getLinkKey() + ") title=" + lti.ltiLinkTitle);
        }

        boolean userChanged = false;
        if (lti.ltiUserDisplayName != null && lti.user != null && !lti.ltiUserDisplayName.equals(lti.user.getDisplayName())) {
            lti.user.setDisplayName(lti.ltiUserDisplayName);
        }
        if (lti.ltiUserEmail != null && lti.user != null && !lti.ltiUserEmail.equals(lti.user.getEmail())) {
            lti.user.setEmail(lti.ltiUserEmail);
        }
        if (userChanged) {
            lti.user = repos.users.save(lti.user);
            updates++;
            log.info("LTIupdate: Updated lti.user (id=" + lti.user.getUserKey() + ") name=" + lti.ltiUserDisplayName + ", email=" + lti.ltiUserEmail);
        }

        if (lti.rawUserRoles != null && lti.userRoleNumber != lti.membership.getRole()) {
            lti.membership.setRole(lti.userRoleNumber);
            lti.membership = repos.members.save(lti.membership);
            updates++;
            log.info("LTIupdate: Updated membership (id=" + lti.membership.getMembershipId() + ", user=" + lti.ltiUserId + ", context=" + lti.ltiContextId + ") roles=" + lti.rawUserRoles + ", role=" + lti.userRoleNumber);
        }

        // need to recheck and see if we are complete now
        lti.checkCompleteLTIRequest(true);

        lti.loadingUpdates = inserts + updates;
        lti.updated = true;
        log.info("LTIupdate: changes=" + lti.loadingUpdates + ", inserts=" + inserts + ", updates=" + updates);
        return lti.loadingUpdates;
    }

    public KeyRequestEntity findKeyRequest(LTIRequest lti) {
        assert lti != null;
        assert lti.getUser() != null : "User not populated in the LTIRequest";
        return repos.keyRequests.findByUser_UserId(lti.getUser().getUserId());
    }

}