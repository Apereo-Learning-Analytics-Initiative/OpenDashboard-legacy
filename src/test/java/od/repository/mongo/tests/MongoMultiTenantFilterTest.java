package od.repository.mongo.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import od.exception.MissingCookieException;
import od.repository.mongo.MongoMultiTenantFilter;
import od.repository.mongo.MultiTenantMongoDbFactory;
import od.test.groups.ModelUnitTests;

@Category(ModelUnitTests.class)
public class MongoMultiTenantFilterTest extends MongoTests{

    private static final String OATH_CONSUMER_KEY = "OATH_CONSUMER_KEY";
    private static final String COOKIE_NAME = "OD_T";
    
    @Mock 
    HttpServletRequest req;
    @Mock
    HttpServletResponse res;
    @Mock
    FilterChain fc;
    @Mock
    CookieGenerator cookieGenerator;
    @InjectMocks
    MongoMultiTenantFilter mongoMultiTenantFilter;
    
    Exception exception;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mongoMultiTenantFilter, "cookieName", COOKIE_NAME);
    }
    
    // Mockito will fail on a successive test, if the tests are simple
    // Use the below validation to ensure the failure was due to the correct test
    @After
    public void validate() {
        Mockito.validateMockitoUsage();
    }

    @SuppressWarnings("static-access")
    @Test
    public void doInternalFilterWillSetCookieCallDoFilterOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("oauth_consumer_key", OATH_CONSUMER_KEY);

        mongoMultiTenantFilter.doFilterInternal(request, res, fc);

        verify(cookieGenerator, times(1)).setCookieName(COOKIE_NAME);
        verify(cookieGenerator, times(1)).setCookiePath("/");
        verify(cookieGenerator, times(1)).setCookieMaxAge(86400);
        verify(cookieGenerator, times(1)).addCookie(res, Base64.encodeBase64String(OATH_CONSUMER_KEY.getBytes("UTF-8")));

        verify(fc, times(1)).doFilter(request, res);
    }

    @SuppressWarnings("static-access")
    @Test
    public void doInternalFilterWillNotThrowExceptionWhenGivenExpectedCookieNameNotOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie testCookie = new Cookie(COOKIE_NAME, OATH_CONSUMER_KEY);
        request.setParameter("oauth_consumer_key", "");
        request.setCookies(testCookie);
        try{
            mongoMultiTenantFilter.doFilterInternal(request, res, fc);
        }catch (Exception ex){
            exception = ex;
        }
        verify(fc, times(1)).doFilter(request, res);
        assertEquals(null, exception);
    }

    @Test(expected = MissingCookieException.class)
    public void doInternalFilterWillThrowMissingCookieExceptionWhenNoCookieValueBlankOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie testCookie = new Cookie("NOT_COOKIE_NAME", OATH_CONSUMER_KEY);
        request.setParameter("oauth_consumer_key", "");
        request.setCookies(testCookie);

        //Ensure test object is correct
        assertEquals(WebUtils.getCookie(request, COOKIE_NAME), null);

        mongoMultiTenantFilter.doFilterInternal(request, response, fc);
    }

    @Test(expected = MissingCookieException.class)
    public void doInternalFilterWillThrowMissingCookieExceptionWhenNoCookieIsBlanckOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie testCookie = new Cookie(COOKIE_NAME, "");
        request.setParameter("oauth_consumer_key", "");
        request.setCookies(testCookie);

        //Ensure test object is correct
        assertEquals(WebUtils.getCookie(request, COOKIE_NAME).getValue(), "");

        mongoMultiTenantFilter.doFilterInternal(request, response, fc);
    }
}
