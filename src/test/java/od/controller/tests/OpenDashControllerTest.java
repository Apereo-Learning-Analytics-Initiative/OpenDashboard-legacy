package od.controller.tests;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import lti.LaunchRequest;
import od.OpenDashController;
import od.repository.SessionRepositoryInterface;
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

public class OpenDashControllerTest extends ControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(OpenDashControllerTest.class);

    @Mock
    private SessionRepositoryInterface sessionRepository;
    @Mock
    private od.framework.model.Session openDashSession;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private OpenDashController openDashController;

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
        openDashController.setAuthenticationManager(authenticationManager);
        this.mockMvc = this.createMockMvc(openDashController);
    }

    @Test
    public void doesLtiReturnProperModelAndViewWhenGivenValidInput() throws Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();
        given(sessionRepository.save((od.framework.model.Session)anyObject())).willReturn(openDashSession);
        given(openDashSession.getId()).willReturn("sessionId");

        mockMvc.perform(post("/")
                        .session(session)
                        .param("roles", "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testObjectMapper.writeValueAsString(launchRequest)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("index"))
                        .andExpect(model().size(2))
                        .andExpect(model().attributeExists("inbound_lti_launch_request", "token"));
    }

    @Test
    public void doesLtiReturnExceptionWhenExceptionIsThrown() throws JsonProcessingException, Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();
        serviceFailureExpectedResponse.setUrl("http://localhost/");
        given(authenticationManager.authenticate((Authentication)anyObject())).willThrow(new RuntimeException(EXCEPTION_MESSAGE));

        MvcResult actualResult = mockMvc.perform(post("/")
                                .session(session)
                                .param("roles", "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testObjectMapper.writeValueAsString(launchRequest)))
                                .andReturn();

        Response actualResponse = this.getResponseFromMockMvcModelAndViewResult(actualResult);
        validateActualResponseAgainstExpectedResponse(serviceFailureExpectedResponse, actualResponse);
    }

    @Test
    public void doesRoutesReturnProperModelAndViewWhenGivenValidInput() throws Exception {
        LaunchRequest launchRequest = this.createLaunchRequest();

        mockMvc.perform(get("/")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testObjectMapper.writeValueAsString(launchRequest)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("index"))
                        .andExpect(model().size(0));
    }

}
