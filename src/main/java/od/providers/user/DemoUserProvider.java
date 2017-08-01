/**
 * 
 */
package od.providers.user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.enrollment.EnrollmentProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

/**
 * @author ggilbert
 *
 */
@Component("user_demo")
public class DemoUserProvider implements UserProvider {

  private static final Logger log = LoggerFactory.getLogger(DemoUserProvider.class);
  
  private static final String KEY = "user_demo";
  private static final String BASE = "OD_DEMO_USER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  @Autowired private ProviderService providerService;
  private Map<String, User> users;
  
  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public ProviderConfiguration getProviderConfiguration() {
    // Not needed for demo provider
    return new ProviderConfiguration() {
      
      @Override
      public LinkedList<ProviderConfigurationOption> getOptions() {
        return new LinkedList<>();
      }
      
      @Override
      public ProviderConfigurationOption getByKey(String key) {
        return null;
      }
    };
  }
  
  @PostConstruct
  public void init() throws ProviderException {
    users = new HashMap<>();
    EnrollmentProvider enrollmentProvider = providerService.getRosterProviders().get("roster_demo");
    String [] classes = {"demo-class-1","demo-class-2","demo-class-3"};
    for (String klass : classes) {
      Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForClass(null, klass, true);
      for (Enrollment enrollment : enrollments) {
        users.put(enrollment.getUser().getSourcedId(), enrollment.getUser());
      }
    }
  }

  @Override
  public User getUserBySourcedId(ProviderData providerData, String userSourcedId) {
    log.debug("{}",users);
    return users.get(userSourcedId);
  }

  @Override
  public String getUserSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
    return "teacher-sourcedId-1";
  }

}
