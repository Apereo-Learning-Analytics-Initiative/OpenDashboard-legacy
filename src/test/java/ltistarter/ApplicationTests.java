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

import ltistarter.model.LtiKeyEntity;
import ltistarter.repository.LtiKeyRepository;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    ConfigurableWebApplicationContext context;

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    LtiKeyRepository ltiKeyRepository;

    // A few tests here just to verify that things are wired up correctly

    @Test
    public void contextLoads() {
        assertNotNull(context);
    }

    @Test
    public void testJPA() {
        Iterable<LtiKeyEntity> keys;
        assertNotNull(ltiKeyRepository);
        keys = ltiKeyRepository.findAll();
        assertFalse(keys.iterator().hasNext());

        ltiKeyRepository.save(new LtiKeyEntity("key", "secret"));
        ltiKeyRepository.save(new LtiKeyEntity("AZkey", "AZsecret"));
        keys = ltiKeyRepository.findAll();
        assertTrue(keys.iterator().hasNext());
        assertEquals(2, CollectionUtils.size(keys.iterator()));
    }

}
