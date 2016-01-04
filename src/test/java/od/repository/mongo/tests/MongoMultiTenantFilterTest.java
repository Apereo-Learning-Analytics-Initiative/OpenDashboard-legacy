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

import od.TenantService;
import od.exception.MissingCookieException;
import od.exception.MissingTenantException;
import od.repository.mongo.MongoMultiTenantFilter;
import od.repository.mongo.MultiTenantMongoDbFactory;
import od.test.groups.ModelUnitTests;

@Category(ModelUnitTests.class)
public class MongoMultiTenantFilterTest extends MongoTests{

    private static final String COOKIE_NAME = "X-OD-TENANT";
    
    @Mock 
    HttpServletRequest req;
    @Mock
    HttpServletResponse res;
    @Mock
    FilterChain fc;
    @Mock
    TenantService tenantService;
    @InjectMocks
    MongoMultiTenantFilter mongoMultiTenantFilter;
    
    Exception exception;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
        request.setParameter("oauth_consumer_key", "tenant1");

        mongoMultiTenantFilter.doFilterInternal(request, res, fc);

        verify(fc, times(1)).doFilter(request, res);
    }

    @SuppressWarnings("static-access")
    @Test
    public void doInternalFilterWillNotThrowExceptionWhenGivenExpectedCookieNameNotOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie testCookie = new Cookie(COOKIE_NAME, "tenant1");
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

    @Test(expected = MissingTenantException.class)
    public void doInternalFilterWillThrowMissingTenantExceptionWhenNoCookieValueBlankOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie testCookie = new Cookie("NOT_COOKIE_NAME", "tenant1");
        request.setParameter("oauth_consumer_key", "");
        request.setCookies(testCookie);

        //Ensure test object is correct
        assertEquals(WebUtils.getCookie(request, COOKIE_NAME), null);

        mongoMultiTenantFilter.doFilterInternal(request, response, fc);
    }

    @Test
    public void doInternalFilterWillThrowMissingTenantExceptionWhenNoCookieIsBlanckOnLtiLaunch() throws ServletException, IOException, MissingCookieException{
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
