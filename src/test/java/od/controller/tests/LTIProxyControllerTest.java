package od.controller.tests;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import lti.LaunchRequest;
import lti.ProxiedLaunch;
import od.cards.lti.LTIProxyController;
import od.model.Card;
import od.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;
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

public class LTIProxyControllerTest extends ControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(LTIProxyControllerTest.class);

    @Mock
    private ContextMapping contextMapping;
    @Mock
    private Card card;
    @Mock
    private ContextMappingRepositoryInterface contextMappingRepository;

    @InjectMocks
    private LTIProxyController ltiProxyController;

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
        this.mockMvc = this.createMockMvc(ltiProxyController);
    }

    @Test
    public void doesProxyReturnProperJsonWhenGivenValidInput() throws Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();
        Map<String, Object> config = createConfig();

        given(contextMappingRepository.findOne("contextMappingId")).willReturn(contextMapping);
        given(contextMapping.findCard("cardId")).willReturn(card);
        given(card.getConfig()).willReturn(config);

        mockMvc.perform(post("/api/{contextMappingId}/lti/launch/{cardId}", "contextMappingId", "cardId")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(launchRequest)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(JSON_CONTENT_TYPE))
                        .andExpect(content().string(testObjectMapper.writeValueAsString(createProxyLaunchRequest())));
    }

    @Test
    public void doesProxyReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();
        Map<String, Object> config = createConfig();
        serviceFailureExpectedResponse.setUrl("http://localhost/api/contextMappingId/lti/launch/cardId");

        given(contextMappingRepository.findOne("contextMappingId")).willThrow(new RuntimeException(EXCEPTION_MESSAGE));
        given(contextMapping.findCard("cardId")).willReturn(card);
        given(card.getConfig()).willReturn(config);

        MvcResult actualResult = executeMockMvcPostAjaxServiceExceptionAndPartialValidation("/api/{contextMappingId}/lti/launch/{cardId}", "contextMappingId", "cardId", launchRequest, mockMvc);

        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
    }

    private Map<String, Object> createConfig() {
        Map<String,Object> config = new HashMap<>();
        config.put("launchUrl", "http://test.com");
        config.put("key", "test");
        config.put("secret", "test");
        return config;
    }

    private ProxiedLaunch createProxyLaunchRequest() {
        LaunchRequest launchRequest = this.createLaunchRequest();
        LaunchRequest proxiedLaunchRequest = new LaunchRequest(
                launchRequest.getLti_message_type(),
                launchRequest.getLti_version(),
                launchRequest.getResource_link_id(),
                launchRequest.getContext_id(),
                null, //Launch_presentation_document_target
                null, //Launch_presentation_width
                null, //Launch_presentation_height
                null, //Launch_presentation_return_url
                launchRequest.getUser_id(),
                launchRequest.getRoles(),
                launchRequest.getContext_type(),
                launchRequest.getLaunch_presentation_locale(),
                null, //Launch_presentation_css_url
                launchRequest.getRole_scope_mentor(),
                launchRequest.getUser_image(),
                null, // custom
                null, // ext
                launchRequest.getResource_link_title(),
                null, // resource_link_description
                launchRequest.getLis_person_name_given(),
                launchRequest.getLis_person_name_family(),
                launchRequest.getLis_person_name_full(),
                launchRequest.getLis_person_contact_email_primary(),

                // TODO make passing outcomes related configurable
                null, //lis_outcome_service_url
                null, //lis_result_sourcedid

                launchRequest.getContext_title(),
                launchRequest.getContext_label(),
                null, //Tool_consumer_info_product_family_code
                null, //Tool_consumer_info_version
                "OpenDashboard", //Tool_consumer_instance_guid
                null, //Tool_consumer_instance_name
                null, //Tool_consumer_instance_description
                null, //Tool_consumer_instance_url
                null, //Tool_consumer_instance_contact_email
                "test", // oauth_consumer_key
                launchRequest.getOauth_signature_method(),
                launchRequest.getOauth_timestamp(),
                launchRequest.getOauth_nonce(),
                launchRequest.getOauth_version(),
                null, // null oauth_signature is intentional; we calculate it later
                launchRequest.getOauth_callback());
        SortedMap<String,String> sortedParams = proxiedLaunchRequest.toSortedMap();
        sortedParams.put("oauth_signature", "UZSEW8BE9AMHuwfE2sNaOC5+4E8=");
        ProxiedLaunch proxyLaunch = new ProxiedLaunch(sortedParams, "http://test.com");

        return proxyLaunch;
    }

}
