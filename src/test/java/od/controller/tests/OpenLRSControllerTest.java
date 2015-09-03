package od.controller.tests;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import lti.LaunchRequest;
import od.cards.openlrs.OpenLRSCardController;
import od.cards.openlrs.OpenLRSCardController.RestTemplateFactoryInterface;
import od.framework.model.Card;
import od.framework.model.ContextMapping;
import od.repository.ContextMappingRepositoryInterface;
import od.utils.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

public class OpenLRSControllerTest extends ControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(OpenLRSControllerTest.class);

    @Mock
    private ContextMapping contextMapping;
    @Mock
    private Card card;
    @Mock
    private RestTemplateFactoryInterface restTemplateFactory;
    @Mock
    private RestTemplate restTemplate;
    @SuppressWarnings("rawtypes")
    @Mock
    private ResponseEntity response;
    @Mock
    private ContextMappingRepositoryInterface contextMappingRepository;

    @InjectMocks
    private OpenLRSCardController openLRSController;

    private MockMvc mockMvc;
    private Response serviceFailureExpectedResponse;

    // Setting up to have the spring app context in place while still being able to mock/control
    // the services the controller uses.
    @SuppressWarnings("unchecked")
    @Override
    @Before
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
        serviceFailureExpectedResponse = createServiceCallExceptionExpectedResponse();
        session = this.createSession();
        openLRSController.setRestTemplateFactory(restTemplateFactory);
        given(restTemplateFactory.createRestTemplate()).willReturn(restTemplate);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), (HttpEntity<String>)anyObject(), anyClass())).willReturn(response);
        given(response.getBody()).willReturn("body");
        this.mockMvc = this.createMockMvc(openLRSController);
    }

    @Test
    public void doesGetOpenLRSStatementsReturnProperJsonWhenGivenValidInput() throws Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();
        Map<String, Object> config = createConfig();

        given(contextMappingRepository.findOne("contextMappingId")).willReturn(contextMapping);
        given(contextMapping.findCard("cardId")).willReturn(card);
        given(contextMapping.getContext()).willReturn("contextId");
        given(card.getConfig()).willReturn(config);

        mockMvc.perform(get("/api/{contextMappingId}/db/{dashboardId}/openlrs/{cardId}/statements", "contextMappingId", "dashboardId", "cardId")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUESTED_WITH_HEADER_NAME, X_REQUESTED_WITH_AJAX_VALUE)
                        .content(testObjectMapper.writeValueAsString(launchRequest)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("body"));
    }

    @Test
    public void doesGetOpenLRSStatementsReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
        serviceFailureExpectedResponse.setUrl("http://localhost/api/contextMappingId/db/dashboardId/openlrs/cardId/statements");
        given(contextMappingRepository.findOne("contextMappingId")).willThrow(new RuntimeException(EXCEPTION_MESSAGE));

        MvcResult actualResult = executeMockMvcGetAjaxServiceExceptionAndPartialValidation("/api/{contextMappingId}/db/{dashboardId}/openlrs/{cardId}/statements", "contextMappingId", "dashboardId", "cardId", mockMvc);

        Response actualResponse = this.getResponseFromMockMvcAjaxResult(actualResult);
        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
    }

    private Map<String, Object> createConfig() {
        Map<String,Object> config = new HashMap<>();
        config.put("launchUrl", "http://test.com");
        config.put("key", "test");
        config.put("secret", "test");
        config.put("url", "http://test.com");
        return config;
    }

}
