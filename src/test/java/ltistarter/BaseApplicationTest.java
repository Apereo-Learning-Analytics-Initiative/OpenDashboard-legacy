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

import ltistarter.config.ApplicationConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("testing") // make the active profile "testing"
public abstract class BaseApplicationTest {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public ApplicationConfig applicationConfig;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public ConfigurableWebApplicationContext context;

    @PostConstruct
    public void init() {
        applicationConfig.getEnvironment().setActiveProfiles("testing");
    }

    @Test
    public void checkSpring() {
        assertNotNull(context);
        assertNotNull(applicationConfig);
        assertTrue(applicationConfig.getEnvironment().acceptsProfiles("testing"));
    }

}