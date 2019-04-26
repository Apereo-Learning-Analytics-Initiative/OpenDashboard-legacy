/**
 * 
 */
package od.providers.course;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.enrollment.DemoEnrollmentProvider;

import org.apereo.lai.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;

/**
 * @author ggilbert
 *
 */
@Component("course_demo")
public class DemoClassProvider implements CourseProvider {

  private static final Logger log = LoggerFactory.getLogger(DemoClassProvider.class);
  
  private static final String KEY = "course_demo";
  private static final String BASE = "OD_DEMO_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
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

  @Override
  public Course getContext(ProviderData providerData, String contextId) throws ProviderException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Course> getContexts(ProviderData providerData, String userId) throws ProviderException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getClassSourcedIdWithExternalId(Tenant tenant, String externalId) throws ProviderException {
     return "demo-class-3";
  }

  @Override
  public Class getClass(Tenant tenant, String classSourcedId) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

}
