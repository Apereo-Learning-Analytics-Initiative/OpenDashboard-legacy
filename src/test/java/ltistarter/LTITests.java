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

import ltistarter.lti.LTIDataService;
import ltistarter.lti.LTIRequest;
import ltistarter.model.*;
import ltistarter.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@SuppressWarnings({"UnusedAssignment", "SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LTITests extends BaseApplicationTest {

    @Autowired
    LtiKeyRepository ltiKeyRepository;
    @Autowired
    LtiUserRepository ltiUserRepository;
    @Autowired
    LtiContextRepository ltiContextRepository;
    @Autowired
    LtiLinkRepository ltiLinkRepository;
    @Autowired
    LtiMembershipRepository ltiMembershipRepository;
    @Autowired
    LtiServiceRepository ltiServiceRepository;

    @Autowired
    LTIDataService ltiDataService;

    @Test
    @Transactional
    public void testLTIRequest() {
        assertNotNull(ltiDataService);
        assertNotNull(ltiKeyRepository);
        assertNotNull(ltiContextRepository);
        assertNotNull(ltiLinkRepository);
        assertNotNull(ltiUserRepository);
        assertNotNull(ltiMembershipRepository);
        MockHttpServletRequest request;
        LTIRequest ltiRequest;

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

        LtiServiceEntity service11 = ltiServiceRepository.save(new LtiServiceEntity("grading", key1, "format"));
        LtiServiceEntity service12 = ltiServiceRepository.save(new LtiServiceEntity("tracking", key1, "format"));

        request = new MockHttpServletRequest(); // NOT LTI request
        try {
            ltiRequest = new LTIRequest(request);
            fail("Should have died");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        request = new MockHttpServletRequest(); // LTI request (minimal)
        request.setParameter(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0);
        request.setParameter(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC);
        request.setParameter(LTIRequest.LTI_CONSUMER_KEY, key1.getKeyKey());
        ltiRequest = new LTIRequest(request);
        assertFalse(ltiRequest.isComplete());
        assertNotNull(ltiRequest.getLtiVersion());
        assertNotNull(ltiRequest.getLtiMessageType());
        assertNotNull(ltiRequest.getLtiConsumerKey());
        assertNull(ltiRequest.getKey()); // not loaded yet
        boolean loaded = ltiDataService.loadLTIDataFromDB(ltiRequest); // load up the data
        assertTrue(loaded);
        assertNotNull(ltiRequest.getKey());
        assertEquals(key1, ltiRequest.getKey());

        request = new MockHttpServletRequest(); // LTI request (full)
        request.setParameter(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0);
        request.setParameter(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC);
        request.setParameter(LTIRequest.LTI_CONSUMER_KEY, key1.getKeyKey());
        request.setParameter(LTIRequest.LTI_CONTEXT_ID, context1.getContextKey());
        request.setParameter(LTIRequest.LTI_LINK_ID, link1.getLinkKey());
        request.setParameter(LTIRequest.LTI_USER_ID, user1.getUserKey());
        ltiRequest = new LTIRequest(request, ltiDataService, false);
        assertTrue(ltiRequest.isLoaded());
        assertTrue(ltiRequest.isComplete());
        assertNotNull(ltiRequest.getLtiVersion());
        assertNotNull(ltiRequest.getLtiMessageType());
        assertNotNull(ltiRequest.getLtiConsumerKey());
        assertNotNull(ltiRequest.getKey());
        assertEquals(key1, ltiRequest.getKey());
        assertNotNull(ltiRequest.getContext());
        assertNotNull(ltiRequest.getLink());
        assertNotNull(ltiRequest.getLtiUserId());
        assertNotNull(ltiRequest.getMembership());
        assertNull(ltiRequest.getService());
        assertNull(ltiRequest.getResult());

        request = new MockHttpServletRequest(); // LTI request (gaps)
        request.setParameter(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0);
        request.setParameter(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC);
        request.setParameter(LTIRequest.LTI_CONSUMER_KEY, key1.getKeyKey());
        request.setParameter(LTIRequest.LTI_CONTEXT_ID, context1.getContextKey());
        request.setParameter(LTIRequest.LTI_LINK_ID, "invalid_link");
        request.setParameter(LTIRequest.LTI_USER_ID, user1.getUserKey());
        request.setParameter(LTIRequest.LTI_SOURCEDID, "invalid_sourcedid");
        request.setParameter(LTIRequest.LTI_SERVICE, service11.getServiceKey());
        ltiRequest = new LTIRequest(request, ltiDataService, false);
        assertTrue(ltiRequest.isLoaded());
        assertFalse(ltiRequest.isComplete()); // missing the link
        assertFalse(ltiRequest.isUpdated());
        assertNotNull(ltiRequest.getLtiVersion());
        assertNotNull(ltiRequest.getLtiMessageType());
        assertNotNull(ltiRequest.getLtiConsumerKey());
        assertNotNull(ltiRequest.getKey());
        assertNotNull(ltiRequest.getContext());
        assertNull(ltiRequest.getLink());
        assertNotNull(ltiRequest.getLtiUserId());
        assertNotNull(ltiRequest.getMembership());
        assertNull(ltiRequest.getResult());
        assertNotNull(ltiRequest.getService());

        // testing updating existing data
        request = new MockHttpServletRequest(); // LTI request (gaps)
        request.setParameter(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0);
        request.setParameter(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC);
        request.setParameter(LTIRequest.LTI_CONSUMER_KEY, key1.getKeyKey());
        request.setParameter(LTIRequest.LTI_CONTEXT_ID, context1.getContextKey());
        request.setParameter(LTIRequest.LTI_CONTEXT_TITLE, "AZ context 1");
        request.setParameter(LTIRequest.LTI_LINK_ID, link1.getLinkKey());
        request.setParameter(LTIRequest.LTI_LINK_TITLE, "AZ link 1");
        request.setParameter(LTIRequest.LTI_USER_ID, user1.getUserKey());
        request.setParameter(LTIRequest.LTI_USER_NAME_FULL, "Aaron Zeckoski");
        request.setParameter(LTIRequest.LTI_USER_EMAIL, "azeckoski@fake.email.com");
        request.setParameter(LTIRequest.LTI_USER_ROLES, "Administrator,Instructor,Learner");
        request.setParameter(LTIRequest.LTI_SOURCEDID, "new_sourcedid_AZ");
        request.setParameter(LTIRequest.LTI_SERVICE, service11.getServiceKey());
        ltiRequest = new LTIRequest(request, ltiDataService, true);
        assertTrue(ltiRequest.isLoaded());
        assertTrue(ltiRequest.isComplete());
        assertTrue(ltiRequest.isUpdated());
        assertEquals(2, ltiRequest.getUserRoleNumber());
        assertNotNull(ltiRequest.getLtiVersion());
        assertNotNull(ltiRequest.getLtiMessageType());
        assertNotNull(ltiRequest.getLtiConsumerKey());
        assertNotNull(ltiRequest.getKey());
        assertNotNull(ltiRequest.getContext());
        assertNotNull(ltiRequest.getLink());
        assertNotNull(ltiRequest.getUser());
        assertNotNull(ltiRequest.getMembership());
        assertNotNull(ltiRequest.getResult());
        assertNotNull(ltiRequest.getService());

        // testing inserting all new data (except key of course)
        request = new MockHttpServletRequest(); // LTI request (gaps)
        request.setParameter(LTIRequest.LTI_VERSION, LTIRequest.LTI_VERSION_1P0);
        request.setParameter(LTIRequest.LTI_MESSAGE_TYPE, LTIRequest.LTI_MESSAGE_TYPE_BASIC);
        request.setParameter(LTIRequest.LTI_CONSUMER_KEY, key5.getKeyKey());
        request.setParameter(LTIRequest.LTI_CONTEXT_ID, "context 5555");
        request.setParameter(LTIRequest.LTI_CONTEXT_TITLE, "AZ context 5555");
        request.setParameter(LTIRequest.LTI_LINK_ID, "link 5555");
        request.setParameter(LTIRequest.LTI_LINK_TITLE, "AZ link 5555");
        request.setParameter(LTIRequest.LTI_USER_ID, "rzeckoski");
        request.setParameter(LTIRequest.LTI_USER_NAME_FULL, "Rebecca Zeckoski");
        request.setParameter(LTIRequest.LTI_USER_EMAIL, "rzeckoski@fake.email.com");
        request.setParameter(LTIRequest.LTI_USER_ROLES, "Mentor/Advisor,Instructor/GuestInstructor,TeachingAssistant/TeachingAssistant");
        request.setParameter(LTIRequest.LTI_SOURCEDID, "RZ_new_sourcedid");
        request.setParameter(LTIRequest.LTI_SERVICE, "RZ_grading");
        ltiRequest = new LTIRequest(request, ltiDataService, true);
        assertTrue(ltiRequest.isLoaded());
        assertTrue(ltiRequest.isComplete());
        assertTrue(ltiRequest.isUpdated());
        assertNotNull(ltiRequest.getLtiVersion());
        assertNotNull(ltiRequest.getLtiMessageType());
        assertNotNull(ltiRequest.getLtiConsumerKey());
        assertNotNull(ltiRequest.getKey());
        assertNotNull(ltiRequest.getContext());
        assertNotNull(ltiRequest.getLink());
        assertNotNull(ltiRequest.getUser());
        assertNotNull(ltiRequest.getMembership());
        assertNotNull(ltiRequest.getResult());
        assertNotNull(ltiRequest.getService());
    }

}