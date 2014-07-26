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
package ltistarter.controllers;

import ltistarter.Application;
import ltistarter.BaseApplicationTest;
import ltistarter.lti.LTIRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class AppControllersTest extends BaseApplicationTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        // Setup Spring test in webapp-mode (same config as spring-boot)
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testLoadRoot() throws Exception {
        // Test basic home controller request
        MvcResult result = this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("Hello Spring Boot"));
    }

    @Test
    @Ignore
    public void testLoadLTI() throws Exception {
        // test minimal LTI launch
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_OAUTH"));
        authorities.add(new SimpleGrantedAuthority("ROLE_LTI"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authToken = new UsernamePasswordAuthenticationToken("azeckoski", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        this.mockMvc.perform(
                post("/ltip0")
                        .param(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0)
                        .param(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC)
                        .param(LTIRequest.LTI_CONSUMER_KEY, "key")
                        .param(LTIRequest.LTI_LINK_ID, "Mylink")
                        .param(LTIRequest.LTI_CONTEXT_ID, "courseAZ")
                        .param(LTIRequest.LTI_USER_ID, "azeckoski")
        ).andExpect(status().isOk());
    }

}