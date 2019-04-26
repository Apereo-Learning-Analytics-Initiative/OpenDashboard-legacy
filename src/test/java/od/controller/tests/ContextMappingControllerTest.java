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
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import od.framework.api.ContextMappingController;
import od.framework.model.ContextMapping;
import od.repository.mongo.ContextMappingRepository;
import od.utils.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ContextMappingControllerTest extends ControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(ContextMappingControllerTest.class);

    @Mock
    private ContextMappingRepository contextMappingRepository;

    @InjectMocks
    private ContextMappingController contextMappingController;

    private MockMvc mockMvc;
    private Response serviceFailureExpectedResponse;
    private ContextMapping contextMapping;

    // Setting up to have the spring app context in place while still being able to mock/control
    // the services the controller uses.
    @Override
    @Before
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
        session = this.createSession();
        serviceFailureExpectedResponse = createServiceCallExceptionExpectedResponse();
        contextMapping = new ContextMapping();
        this.mockMvc = this.createMockMvc(contextMappingController);
    }

//    @Test
//    public void doesCreateReturnProperJsonWhenGivenValidInput() throws Exception {
//        given(contextMappingRepository.save((ContextMapping) anyObject())).willReturn(contextMapping);
//
//        mockMvc.perform(post("/api/consumer/{consumerKey}/context", "consumerKey")
//                        .session(session)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                        .andExpect(status().isOk())
//                        .andExpect(content().string(testObjectMapper.writeValueAsString(contextMapping)));
//    }

//    @Test
//    public void doesCreateReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
//        serviceFailureExpectedResponse.setUrl("http://localhost/api/consumer/consumerKey/context");
//
//        given(contextMappingRepository.save((ContextMapping) anyObject())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));
//
//        MvcResult actualResult = mockMvc.perform(post("/api/consumer/{consumerKey}/context", "consumerKey").session(session)
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                                        .andReturn();
//
//        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
//        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
//    }
//
//    @Test
//    public void doesUpdateReturnProperJsonWhenGivenValidInput() throws Exception {
//        given(contextMappingRepository.save((ContextMapping) anyObject())).willReturn(contextMapping);
//
//        mockMvc.perform(put("/api/consumer/{consumerKey}/context/{context}", "consumerKey", "context")
//                        .session(session)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                        .andExpect(status().isOk())
//                        .andExpect(content().string(testObjectMapper.writeValueAsString(contextMapping)));
//    }

//    @Test
//    public void doesUpdateReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
//        serviceFailureExpectedResponse.setUrl("http://localhost/api/consumer/consumerKey/context/context");
//
//        given(contextMappingRepository.save((ContextMapping) anyObject())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));
//
//        MvcResult actualResult = mockMvc.perform(put("/api/consumer/{consumerKey}/context/{context}", "consumerKey", "context")
//                                        .session(session)
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                                        .andReturn();
//
//        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
//        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
//    }

//    @Test
//    public void doesGetReturnProperJsonWhenGivenValidInput() throws Exception {
//        //given(contextMappingRepository.findByKeyAndContext(anyString(), anyString())).willReturn(contextMapping);
//
//        mockMvc.perform(get("/api/consumer/{consumerKey}/context/{context}", "consumerKey", "context")
//                        .session(session)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                        .andExpect(status().isOk())
//                        .andExpect(content().string(testObjectMapper.writeValueAsString(contextMapping)));
//    }

//    @Test
//    public void doesGetReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
//        serviceFailureExpectedResponse.setUrl("http://localhost/api/consumer/consumerKey/context/context");
//
//        //given(contextMappingRepository.findByKeyAndContext(anyString(), anyString())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));
//
//        MvcResult actualResult = mockMvc.perform(get("/api/consumer/{consumerKey}/context/{context}", "consumerKey", "context")
//                                        .session(session)
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
//                                        .content(testObjectMapper.writeValueAsString(contextMapping)))
//                                        .andReturn();
//
//        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
//        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
//    }

    @Test
    public void doesGetByIdReturnProperJsonWhenGivenValidInput() throws Exception {
        given(contextMappingRepository.findOne(anyString())).willReturn(contextMapping);

        mockMvc.perform(get("/api/cm/{id}", "id")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(contextMapping)))
                        .andExpect(status().isOk())
                        .andExpect(content().string(testObjectMapper.writeValueAsString(contextMapping)));
    }

    @Test
    public void doesGetByIdReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
        serviceFailureExpectedResponse.setUrl("http://localhost/api/cm/id");

        given(contextMappingRepository.findOne(anyString())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));

        MvcResult actualResult = mockMvc.perform(get("/api/cm/{id}", "id")
                                        .session(session)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                                        .content(testObjectMapper.writeValueAsString(contextMapping)))
                                        .andReturn();

        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
    }
}
