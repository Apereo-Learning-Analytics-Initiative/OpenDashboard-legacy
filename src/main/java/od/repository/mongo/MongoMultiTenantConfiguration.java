package od.repository.mongo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.util.CookieGenerator;

import com.mongodb.Mongo;

/**
 * @author jbrown
 *
 */
@Profile("mongo-multitenant")
@Configuration
public class MongoMultiTenantConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(MongoMultiTenantConfiguration.class);
  
  @Autowired private MongoMultiTenantFilter mongoFilter;
  
  @Value("${od.defaultDatabaseName:od_default}")
  private String dbName;

  @Bean
  public MongoTemplate mongoTemplate(final Mongo mongo, MultiTenantMongoDbFactory dbFactory) throws Exception {
    MongoTemplate template = new MongoTemplate(mongoDbFactory(mongo));
    dbFactory.setMongoTemplate(template);
    return template;
  }

  @Bean
  public MultiTenantMongoDbFactory mongoDbFactory(final Mongo mongo) throws Exception {
    return new MultiTenantMongoDbFactory(mongo, dbName);
  }
  
  @Bean
  public CookieGenerator cookieGenerator(){
    return new CookieGenerator();
  }
  
  @Bean
  public FilterRegistrationBean mongoFilterBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(mongoFilter);
    List<String> urls = new ArrayList<String>(1);
    urls.add("/");
    urls.add("/api/*");
    urls.add("/cm/*");
    registrationBean.setUrlPatterns(urls);
    registrationBean.setOrder(3);
    registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
    return registrationBean;
  }

}