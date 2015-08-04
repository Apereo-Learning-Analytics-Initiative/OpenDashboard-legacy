package od;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.mongodb.Mongo;

import od.repository.mongo.MongoMultiTenantFilter;
import od.repository.mongo.MultiTenantMongoDbFactory;

/**
 * @author jbrown
 *
 */
@Profile("mongo")
@Configuration
public class MongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);
    @Autowired 
    private MongoMultiTenantFilter mongoFilter;

    @Bean
    public FilterRegistrationBean mongoFilterBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(mongoFilter);
        List<String> urls = new ArrayList<String>(1);
        urls.add("/");
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public MongoTemplate mongoTemplate(final Mongo mongo, MultiTenantMongoDbFactory dbFactory) throws Exception {
        MongoTemplate template = new MongoTemplate(mongoDbFactory(mongo));
        dbFactory.setMongoTemplate(template);
        return template;
    }

    @Bean
    public MultiTenantMongoDbFactory mongoDbFactory(final Mongo mongo) throws Exception {
        return new MultiTenantMongoDbFactory(mongo, "defaultDatabase");
    }
}