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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lti.LaunchRequest;
import od.AbstractTest;
import od.test.groups.ControllerUnitTests;
import od.utils.AppControllerAdvice;
import od.utils.Response;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Category(ControllerUnitTests.class)
public abstract class ControllerTests extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(ControllerTests.class);

    // Leveraging object mapper to avoid writing json
    protected ObjectMapper testObjectMapper;
    protected MockHttpSession session;

    protected static final String ERROR = "error";
    protected static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
    protected static final String RESPONSE = "response";
    protected static final String EXCEPTION_DATA_MESSAGE = "java.lang.RuntimeException: Call Didn't work!!";
    protected static final String EXCEPTION_MESSAGE = "Call Didn't work!!";
    protected static final String X_REQUESTED_WITH_AJAX_VALUE = "XMLHttpRequest";
    protected static final String X_REQUESTED_WITH_HEADER_NAME = "X-Requested-With";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testObjectMapper = new ObjectMapper();
    }

    protected MockHttpSession createSession() {
        MockHttpSession session = new MockHttpSession();
        return session;
    }

    protected Response createServiceCallExceptionExpectedResponse() {
        return createExpectedResponse("", EXCEPTION_MESSAGE, EXCEPTION_DATA_MESSAGE);
    }

    protected Response createExpectedResponse(String url, String error, String data) {
        Response expectedResponse = new Response();
        expectedResponse.setUrl(url);
        expectedResponse.setData(data);
        List<String> errors = new ArrayList<>();
        errors.add(error);
        expectedResponse.setErrors(errors);
        return expectedResponse;
    }

    // Sets up advice controller
    protected ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(AppControllerAdvice.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new AppControllerAdvice(), method);
            }
        };
        exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    protected void validateActualResponseAgainstExpectedResponse(Response expectedResponse, Response actualResponse) throws IOException {
//        logger.info("expected: {}\nactual: {}", expectedResponse.getUrl(), actualResponse.getUrl());
        assertEquals(expectedResponse.getUrl(), actualResponse.getUrl());

        int numberOfErrors = expectedResponse.getErrors().size();
        for (int i = 0 ; i < numberOfErrors; i++) {
//            logger.info("expected: {}\nactual: {}", expectedResponse.getErrors().get(i).toLowerCase(), actualResponse.getErrors().get(i).toLowerCase());
            String expectedError = expectedResponse.getErrors().get(i).toLowerCase(Locale.ENGLISH);
            String actualError = actualResponse.getErrors().get(i).toLowerCase(Locale.ENGLISH);
            assertTrue(actualError.startsWith(expectedError));
        }
//        logger.info("expected: {}\nactual: {}", expectedResponse.getData().toString(), actualResponse.getData().toString());
        String expectedData = expectedResponse.getData().toString();
        String actualData = actualResponse.getData().toString();
        assertTrue(actualData.startsWith(expectedData));
    }

    protected MockMvc createMockMvc(Object controller) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        return MockMvcBuilders.standaloneSetup(controller)
                                      // This is for advice controller to be leveraged
                                      .setHandlerExceptionResolvers(createExceptionResolver())
                                      // This is for circular reference fix (do a get on /login and return to view login fails without this)
                                      .setViewResolvers(viewResolver)
                                      .build();
    }

    protected MockMvc createMockMvcForFilterException(Object controller) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        return MockMvcBuilders.standaloneSetup(controller)
                                      // This is for circular reference fix (do a get on /login and return to view login fails without this)
                                      .setViewResolvers(viewResolver)
                                      .build();
    }

    protected MvcResult executeMockMvcGetModelAndViewServiceExceptionAndPartialValidation(String uri, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri).session(session))
                .andExpect(view().name(ERROR))
                .andExpect(status().isBadRequest())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(RESPONSE))
                .andReturn();
    }

    protected MvcResult executeMockMvcGetModelAndViewServiceExceptionAndPartialValidation(String uri, String uriId, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri, uriId).session(session))
                .andExpect(view().name(ERROR))
                .andExpect(status().isBadRequest())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(RESPONSE))
                .andReturn();
    }

    protected MvcResult executeMockMvcGetModelAndViewServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri, uriId, uriId1).session(session))
                .andExpect(view().name(ERROR))
                .andExpect(status().isBadRequest())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(RESPONSE))
                .andReturn();
    }

    protected MvcResult executeMockMvcGetModelAndViewServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, String uriId2, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri, uriId, uriId1, uriId2).session(session))
                .andExpect(view().name(ERROR))
                .andExpect(status().isBadRequest())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists(RESPONSE))
                .andReturn();
    }

    protected MvcResult executeMockMvcPostAjaxServiceExceptionAndPartialValidation(String uri, String uriId, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post(uri, uriId).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPostAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post(uri, uriId, uriId1).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPostAjaxServiceExceptionAndPartialValidation(String uri, String uriId, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post(uri, uriId).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPostAjaxServiceExceptionAndPartialValidation(String uri, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post(uri).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPutAjaxServiceExceptionAndPartialValidation(String uri, String uriId, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(put(uri, uriId).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPutAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(put(uri, uriId, uriId1).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPutAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, String uriId2, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(put(uri, uriId, uriId1, uriId2).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcPutAjaxServiceExceptionAndPartialValidation(String uri, Object model, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(put(uri).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(model)))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcDeleteAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(delete(uri, uriId, uriId1).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcDeleteAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, String uriId2, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(delete(uri, uriId, uriId1, uriId2).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcGetAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri, uriId, uriId1).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected MvcResult executeMockMvcGetAjaxServiceExceptionAndPartialValidation(String uri, String uriId, String uriId1, String uriId2, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(uri, uriId, uriId1, uriId2).session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE))
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andReturn();
    }

    protected Response getResponseFromMockMvcAjaxResult(MvcResult result) throws JsonParseException, JsonMappingException, IOException {
        String actualResultContent = result.getResponse().getContentAsString();
        return testObjectMapper.readValue(actualResultContent, Response.class);
    }

    protected Response getResponseFromMockMvcModelAndViewResult(MvcResult actualResult) {
        return (Response) actualResult.getModelAndView().getModel().get(RESPONSE);
    }

    protected LaunchRequest createLaunchRequest() {
        LaunchRequest launchRequest = new LaunchRequest();
        launchRequest.setLti_message_type("Lti_message_type");
        launchRequest.setLti_version("Lti_version");
        launchRequest.setResource_link_id("Resource_link_id");
        launchRequest.setContext_id("Context_id");
        launchRequest.setUser_id("User_id");
        launchRequest.setRoles("Roles");
        launchRequest.setContext_type("Context_type");
        launchRequest.setLaunch_presentation_locale("Launch_presentation_locale");
        launchRequest.setRole_scope_mentor("Role_scope_mentor");
        launchRequest.setUser_image("User_image");
        launchRequest.setResource_link_title("Resource_link_title");
        launchRequest.setLis_person_name_given("Lis_person_name_given");
        launchRequest.setLis_person_name_family("Lis_person_name_family");
        launchRequest.setLis_person_name_full("Lis_person_name_full");
        launchRequest.setLis_person_contact_email_primary("Lis_person_contact_email_primary");
        launchRequest.setContext_title("Context_title");
        launchRequest.setContext_label("Context_label");
        launchRequest.setOauth_signature_method("HMAC-SHA1");
        launchRequest.setOauth_timestamp("Oauth_timestamp");
        launchRequest.setOauth_nonce("Oauth_nonce");
        launchRequest.setOauth_version("Oauth_version");
        launchRequest.setOauth_callback("Oauth_callback");
        launchRequest.setRoles("Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator");
        return launchRequest;
    }
}
