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

import ltistarter.model.ConfigEntity;
import ltistarter.repository.ConfigRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationTests extends BaseApplicationTest {

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
    ConfigRepository configRepository;

    @Test
    @Transactional
    public void testConfig() {
        assertNotNull(applicationConfig);
        assertNotNull(configRepository);
        configRepository.save(new ConfigEntity("test.thing", "Value"));
    }

}