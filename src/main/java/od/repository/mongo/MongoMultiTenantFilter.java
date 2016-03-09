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
/**
 *
 */
package od.repository.mongo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.LaunchRequest;
import od.TenantService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * @author jbrown
 *
 */
@ConditionalOnProperty(name="opendashboard.features.multitenant",havingValue="true")
@Component
public class MongoMultiTenantFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(MongoMultiTenantFilter.class);
  private static final String TENANT_PARAMETER = "X-OD-TENANT";
  
  @Value("${od.defaultDatabaseName:od_default}")
  private String defaultDatabase;
  
  @Value("${od.useDefaultDatabaseName:true}")
  private String useDefaultDatabaseName;
  
  @Autowired private TenantService tenantService;
  
  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
    logger.debug("applying MongoMultiTenantFilter");
    logger.debug("allow defaultDatabase: "+useDefaultDatabaseName);
    
    //MultiTenantMongoDbFactory.clearDatabaseNameForCurrentThread();
    String tenant = req.getHeader(TENANT_PARAMETER);
    Cookie tenantCookie = WebUtils.getCookie(req, TENANT_PARAMETER);
    LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());
    
    if (launchRequest != null && StringUtils.isNotBlank(launchRequest.getOauth_consumer_key())) {
      tenant = launchRequest.getOauth_consumer_key();
      tenantCookie = new Cookie(TENANT_PARAMETER, tenant);
      tenantCookie.setPath("/");
      res.addCookie(tenantCookie);
      logger.debug("Tenant value from LTI launch");
    }
    else if (StringUtils.isBlank(tenant) && tenantCookie == null) {
      // Don't know who the tenant is
      if (Boolean.valueOf(useDefaultDatabaseName)) {
        logger.warn("No tenant available in request. Using default database.");
        tenant = defaultDatabase;
      }
      else {
        throw new od.exception.MissingTenantException("No tenant available in request and default database disabled.");
      }
    }
    else if (StringUtils.isBlank(tenant) && tenantCookie != null) {
      tenant = tenantCookie.getValue();
      logger.debug("Tenant value from cookie");
    }
    else if (StringUtils.isNotBlank(tenant) && tenantCookie == null) {
      tenantCookie = new Cookie(TENANT_PARAMETER, tenant);
      tenantCookie.setPath("/");
      res.addCookie(tenantCookie);
      logger.debug("Tenant value from header");
    }
    else {
      // header and cookie
      String tenantValueFromCookie = tenantCookie.getValue();
      if (!tenant.equals(tenantValueFromCookie)) {
        tenantCookie = new Cookie(TENANT_PARAMETER, tenant);
        tenantCookie.setPath("/");
        res.addCookie(tenantCookie);
      }
    }
    
    logger.debug("{}", tenant);
    tenantService.setTenant(tenant);
    //MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(tenant);
    fc.doFilter(req, res);
  }
  
}
