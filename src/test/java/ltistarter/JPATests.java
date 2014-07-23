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
package ltistarter;

import ltistarter.model.*;
import ltistarter.repository.*;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("UnusedAssignment")
@RunWith(SpringJUnit4ClassRunner.class)
public class JPATests extends BaseApplicationTest {

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiKeyRepository ltiKeyRepository;

    @Test
    public void testJPA() {
        Iterable<LtiKeyEntity> keys;
        LtiKeyEntity key;
        int result;
        assertNotNull(ltiKeyRepository);
        keys = ltiKeyRepository.findAll();
        assertFalse(keys.iterator().hasNext());

        ltiKeyRepository.save(new LtiKeyEntity("key", "secret"));
        ltiKeyRepository.save(new LtiKeyEntity("AZkey", "AZsecret"));
        keys = ltiKeyRepository.findAll();
        assertTrue(keys.iterator().hasNext());
        assertEquals(2, CollectionUtils.size(keys.iterator()));
        key = ltiKeyRepository.findByKeyKey("not_real_key");
        assertNull(key);
        key = ltiKeyRepository.findByKeyKey("key");
        assertNotNull(key);
        assertEquals("secret", key.getSecret());
        LtiKeyEntity key2 = ltiKeyRepository.findOne(key.getKeyId());
        assertNotNull(key2);
        assertEquals(key, key2);

        key = ltiKeyRepository.findOne(key.getKeyId());
        assertNotNull(key);

        ltiKeyRepository.delete(key);
        keys = ltiKeyRepository.findAll();
        assertTrue(keys.iterator().hasNext());
        assertEquals(1, CollectionUtils.size(keys.iterator()));

        result = ltiKeyRepository.deleteByKeyKey("not_real_key");
        assertEquals(0, result);
        result = ltiKeyRepository.deleteByKeyKey("AZkey");
        assertEquals(1, result);

        keys = ltiKeyRepository.findAll();
        assertFalse(keys.iterator().hasNext());
        assertEquals(0, CollectionUtils.size(keys.iterator()));
    }

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiUserRepository ltiUserRepository;
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    ProfileRepository profileRepository;
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    SSOKeyRepository ssoKeyRepository;

    @Test
    @Transactional
    public void testJPARelations() {
        Iterable<ProfileEntity> profiles;
        Iterable<LtiUserEntity> users;
        Iterable<SSOKeyEntity> ssoKeys;
        ProfileEntity profile;
        SSOKeyEntity ssoKey;

        assertNotNull(profileRepository);
        assertNotNull(ltiUserRepository);
        assertNotNull(ssoKeyRepository);

        profiles = profileRepository.findAll();
        assertFalse(profiles.iterator().hasNext());
        users = ltiUserRepository.findAll();
        assertFalse(users.iterator().hasNext());
        ssoKeys = ssoKeyRepository.findAll();
        assertFalse(ssoKeys.iterator().hasNext());

        profileRepository.save(new ProfileEntity("AaronZeckoski", null, "azeckoski@test.com"));
        profileRepository.save(new ProfileEntity("BeckyZeckoski", null, "rzeckoski@test.com"));
        profiles = profileRepository.findAll();
        assertTrue(profiles.iterator().hasNext());
        assertEquals(2, CollectionUtils.size(profiles.iterator()));
        profile = profileRepository.findOne(91919l);
        assertNull(profile);
        profile = profileRepository.findByProfileKey("AaronZeckoski");
        assertNotNull(profile);
        assertTrue(profile.getSsoKeys().isEmpty());

        ssoKeyRepository.save(new SSOKeyEntity("random_GOOGLEKEY", "google.com"));
        ssoKeyRepository.save(new SSOKeyEntity("AZ_google_key", "google.com"));
        ssoKeys = ssoKeyRepository.findAll();
        assertTrue(ssoKeys.iterator().hasNext());
        assertEquals(2, CollectionUtils.size(ssoKeys.iterator()));
        ssoKey = ssoKeyRepository.findByKeyKey("AZ_google_key");
        assertNotNull(ssoKey);
        assertNull(ssoKey.getProfile());

        // now add profile to the ssoKey
        ssoKey.setProfile(profile);
        profile.getSsoKeys().add(ssoKey);
        ssoKeyRepository.save(ssoKey);
        ssoKey = ssoKeyRepository.findByKeyKey("AZ_google_key");
        assertNotNull(ssoKey);
        assertNotNull(ssoKey.getProfile());
        profile = ssoKey.getProfile();
        assertFalse(profile.getSsoKeys().isEmpty());
        assertEquals(1, profile.getSsoKeys().size());

        // now remove the ssoKey and make sure things worked
        // TODO more tests
    }

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiContextRepository ltiContextRepository;

    @PersistenceContext
    EntityManager entityManager;

    @SuppressWarnings("JpaQlInspection")
    @Test
    @Transactional
    public void testJPAQuery() {
        assertNotNull(entityManager);
        assertNotNull(ltiKeyRepository);
        assertNotNull(ltiContextRepository);
        Query q;
        List rows;

        ltiKeyRepository.save(new LtiKeyEntity("key", "secret"));
        LtiKeyEntity azkey = ltiKeyRepository.save(new LtiKeyEntity("AZkey", "AZsecret"));

        q = entityManager.createQuery("SELECT k.keyKey FROM LtiKeyEntity k ORDER BY k.keyId");
        List keys = q.getResultList();
        assertNotNull(keys);
        assertEquals(2, keys.size());

        ltiContextRepository.save(new LtiContextEntity("AZcontext", azkey, "AZCtitle", null));

        q = entityManager.createQuery("SELECT k.keyKey, c.contextKey, c.title FROM LtiKeyEntity k LEFT JOIN k.contexts c ORDER BY k.keyId");
        rows = q.getResultList();
        assertNotNull(rows);
        assertEquals(2, rows.size());
        assertEquals(3, ((Object[]) rows.get(0)).length);
        Object[] row1 = ((Object[]) rows.get(1));
        assertEquals(3, row1.length);
        assertEquals("AZkey", row1[0]);
        assertEquals("AZcontext", row1[1]);
        assertEquals("AZCtitle", row1[2]);
    }

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiLinkRepository ltiLinkRepository;
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiMembershipRepository ltiMembershipRepository;

    @SuppressWarnings("JpaQlInspection")
    @Test
    @Transactional
    public void testJPAJoinsQuery() {
        assertNotNull(entityManager);
        assertNotNull(ltiKeyRepository);
        assertNotNull(ltiContextRepository);
        assertNotNull(ltiLinkRepository);
        assertNotNull(ltiUserRepository);
        assertNotNull(ltiMembershipRepository);
        Query q;
        List rows;
        Object[] row;

        LtiKeyEntity key1 = ltiKeyRepository.save(new LtiKeyEntity("AZkey", "AZsecret"));
        LtiKeyEntity key2 = ltiKeyRepository.save(new LtiKeyEntity("key", "secret"));
        LtiKeyEntity key3 = ltiKeyRepository.save(new LtiKeyEntity("3key", "secret"));
        LtiKeyEntity key4 = ltiKeyRepository.save(new LtiKeyEntity("4key", "secret"));
        LtiKeyEntity key5 = ltiKeyRepository.save(new LtiKeyEntity("5key", "secret"));

        LtiUserEntity user1 = ltiUserRepository.save(new LtiUserEntity("azeckoski", null));
        LtiUserEntity user2 = ltiUserRepository.save(new LtiUserEntity("bzeckoski", null));
        LtiUserEntity user3 = ltiUserRepository.save(new LtiUserEntity("czeckoski", null));
        LtiUserEntity user4 = ltiUserRepository.save(new LtiUserEntity("dzeckoski", null));

        LtiContextEntity context1 = ltiContextRepository.save(new LtiContextEntity("AZcontext", key1, "AZCtitle", null));
        LtiContextEntity context2 = ltiContextRepository.save(new LtiContextEntity("3context", key3, "3Ctitle", null));
        LtiContextEntity context3 = ltiContextRepository.save(new LtiContextEntity("5context", key5, "5Ctitle", null));

        LtiLinkEntity link1 = ltiLinkRepository.save(new LtiLinkEntity("AZlink", context1, "linkTitle"));

        LtiMembershipEntity member1 = ltiMembershipRepository.save(new LtiMembershipEntity(context1, user1, LtiMembershipEntity.ROLE_STUDENT));
        LtiMembershipEntity member2 = ltiMembershipRepository.save(new LtiMembershipEntity(context1, user2, LtiMembershipEntity.ROLE_STUDENT));
        LtiMembershipEntity member3 = ltiMembershipRepository.save(new LtiMembershipEntity(context1, user3, LtiMembershipEntity.ROLE_INTRUCTOR));
        LtiMembershipEntity member4 = ltiMembershipRepository.save(new LtiMembershipEntity(context2, user1, LtiMembershipEntity.ROLE_STUDENT));
        LtiMembershipEntity member5 = ltiMembershipRepository.save(new LtiMembershipEntity(context2, user3, LtiMembershipEntity.ROLE_INTRUCTOR));

        // make sure encoding worked
        assertEquals(key1.getKeySha256(), BaseEntity.makeSHA256(key1.getKeyKey()));

        q = entityManager.createQuery("SELECT k FROM LtiKeyEntity k WHERE k.keySha256 = :key");
        q.setParameter("key", key1.getKeySha256());
        LtiKeyEntity lke = (LtiKeyEntity) q.getSingleResult();
        assertNotNull(lke);
        assertEquals(key1, lke);

        String jpql = "SELECT k, c, l, m, u FROM LtiKeyEntity k " +
                "LEFT JOIN k.contexts c ON c.contextSha256 = :context " + // LtiContextEntity
                "LEFT JOIN c.links l ON l.linkSha256 = :link " + // LtiLinkEntity
                "LEFT JOIN c.memberships m " + // LtiMembershipEntity
                "LEFT JOIN m.user u ON u.userSha256 = :user " + // LtiUserEntity
                "WHERE k.keySha256 = :key AND (m IS NULL OR (m.context = c AND m.user = u))";
        q = entityManager.createQuery(jpql);
        // BaseEntity.makeSHA256 is used to be closer to real use but we could have just used the SHA fields like getKeySha256()
        q.setParameter("context", BaseEntity.makeSHA256(context1.getContextKey()));
        q.setParameter("link", BaseEntity.makeSHA256(link1.getLinkKey()));
        q.setParameter("user", BaseEntity.makeSHA256(user1.getUserKey()));
        q.setParameter("key", BaseEntity.makeSHA256(key1.getKeyKey()));
        rows = q.getResultList();
        assertNotNull(rows);
        assertEquals(1, rows.size());
        row = ((Object[]) rows.get(0));
        assertEquals(5, row.length);
        assertNotNull(row[0]);
        assertNotNull(row[1]);
        assertNotNull(row[2]);
        assertNotNull(row[3]);
        assertNotNull(row[4]);

        // no link
        q = entityManager.createQuery(jpql);
        // BaseEntity.makeSHA256 is used to be closer to real use but we could have just used the SHA fields like getKeySha256()
        q.setParameter("context", BaseEntity.makeSHA256(context1.getContextKey()));
        q.setParameter("link", ""); // empty link id
        q.setParameter("user", BaseEntity.makeSHA256(user1.getUserKey()));
        q.setParameter("key", BaseEntity.makeSHA256(key1.getKeyKey()));
        rows = q.getResultList();
        assertNotNull(rows);
        assertEquals(1, rows.size());
        row = ((Object[]) rows.get(0));
        assertEquals(5, row.length);
        assertNotNull(row[0]);
        assertNotNull(row[1]);
        assertNull(row[2]);
        assertNotNull(row[3]);
        assertNotNull(row[4]);

        // no link, user, or memberships
        q = entityManager.createQuery(jpql);
        q.setParameter("context", BaseEntity.makeSHA256(context3.getContextKey()));
        q.setParameter("link", null); // empty link id
        q.setParameter("user", null);
        q.setParameter("key", BaseEntity.makeSHA256(key5.getKeyKey()));
        rows = q.getResultList();
        assertNotNull(rows);
        assertEquals(1, rows.size());
        row = ((Object[]) rows.get(0));
        assertEquals(5, row.length);
        assertNotNull(row[0]);
        assertNotNull(row[1]);
        assertNull(row[2]);
        assertNull(row[3]);
        assertNull(row[4]);

        // only the key
        q = entityManager.createQuery(jpql);
        q.setParameter("context", null);
        q.setParameter("link", null);
        q.setParameter("user", null);
        q.setParameter("key", BaseEntity.makeSHA256(key5.getKeyKey()));
        rows = q.getResultList();
        assertNotNull(rows);
        assertEquals(1, rows.size());
        row = ((Object[]) rows.get(0));
        assertEquals(5, row.length);
        assertNotNull(row[0]);
        assertNull(row[1]);
        assertNull(row[2]);
        assertNull(row[3]);
        assertNull(row[4]);
    }

}