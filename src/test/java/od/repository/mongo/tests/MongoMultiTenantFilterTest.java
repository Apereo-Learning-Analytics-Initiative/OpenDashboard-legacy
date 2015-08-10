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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
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
    MultiTenantMongoDbFactory multiTenantMongoDbFactory;
    @Mock
    WebUtils webUtils;
    @InjectMocks
    MongoMultiTenantFilter mongoMultiTenantFilter;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        //Populate @Value
        ReflectionTestUtils.setField(mongoMultiTenantFilter, "cookieName", COOKIE_NAME);
    }

    @SuppressWarnings("static-access")
    @Test
    public void doInternalFilterWillSetDatabaseNameForCurrentThreadAndCallDoFilterOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("oauth_consumer_key", OATH_CONSUMER_KEY);
        
        mongoMultiTenantFilter.doFilterInternal(mockRequest, res, fc);
        
        verify(fc, times(1)).doFilter(mockRequest, res);
        verify(multiTenantMongoDbFactory, times(1)).setDatabaseNameForCurrentThread(OATH_CONSUMER_KEY);
    }

    @SuppressWarnings("static-access")
    @Test
    public void doInternalFilterWillSetDatabaseNameForCurrentThreadAndCallDoFilterNotOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie testCookie = new Cookie(COOKIE_NAME, OATH_CONSUMER_KEY);
        request.setParameter("oauth_consumer_key", "");
        request.setCookies(testCookie);
        
        mongoMultiTenantFilter.doFilter(request, response, fc);
        verify(multiTenantMongoDbFactory, times(1)).setDatabaseNameForCurrentThread(OATH_CONSUMER_KEY);
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

        mongoMultiTenantFilter.doFilter(request, response, fc);
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

        mongoMultiTenantFilter.doFilter(request, response, fc);
    }
}
