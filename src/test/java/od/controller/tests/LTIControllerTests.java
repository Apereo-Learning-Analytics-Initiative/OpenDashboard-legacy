/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package od.controller.tests;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import lti.LaunchRequest;
import od.entrypoints.LTIEntryPointController;
import od.utils.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;

public class LTIControllerTests extends ControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(LTIControllerTests.class);

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private LTIEntryPointController ltiController;

    private MockMvc mockMvc;
    private Response serviceFailureExpectedResponse;

    // Setting up to have the spring app context in place while still being able to mock/control
    // the services the controller uses.
    @Override
    @Before
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
        session = this.createSession();
        serviceFailureExpectedResponse = createServiceCallExceptionExpectedResponse();
        //openDashController.setAuthenticationManager(authenticationManager);
        this.mockMvc = this.createMockMvc(ltiController);
    }
    
    @Test
    public void doesNothing() {
      
    }

//    @Test
//    public void doesLtiReturnProperModelAndViewWhenGivenValidInput() throws Exception {
//        LaunchRequest launchRequest = this.createLaunchRequest();
//
//        mockMvc.perform(post("/lti")
//                        .session(session)
//                        .param("roles", "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(testObjectMapper.writeValueAsString(launchRequest)))
//                        .andExpect(status().isOk())
//                        .andExpect(view().name("index"))
//                        .andExpect(model().size(1));
//    }
//
//    @Test
//    public void doesLtiReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
//        LaunchRequest launchRequest = this.createLaunchRequest();
//        serviceFailureExpectedResponse.setUrl("http://localhost/lti");
//        given(authenticationManager.authenticate((Authentication)anyObject())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));
//
//        MvcResult actualResult = mockMvc.perform(post("/lti")
//                                .session(session)
//                                .param("roles", "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(testObjectMapper.writeValueAsString(launchRequest)))
//                                .andReturn();
//
//        Response actualResponse = this.getResponseFromMockMvcModelAndViewResult(actualResult);
//        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
//    }
}
