/**
*
*/
package od.repository.mongo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lti.LaunchRequest;
import od.MongoConfiguration;

/**
* @author jbrown
*
*/
@Profile("mongo")
@Component
public class MongoMultiTenantFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(MongoMultiTenantFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req,
           HttpServletResponse res, FilterChain fc)
           throws ServletException, IOException {
     LaunchRequest launchRequest = new LaunchRequest(req.getParameterMap());

     String consumerKey = launchRequest.getOauth_consumer_key();
     MultiTenantMongoDbFactory.setDatabaseNameForCurrentThread(consumerKey);


     fc.doFilter(req, res);
    }
}

