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


}