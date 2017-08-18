/**
 * 
 */
package od.providers.course;

import java.time.LocalDate;
import java.util.HashMap;
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
import unicon.matthews.oneroster.Status;
import unicon.oneroster.Vocabulary;

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
    Map<String, unicon.matthews.oneroster.Class> classes = new HashMap<>();
    
    Map<String, String> metadata1 = new HashMap<>();   
    metadata1.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 23).toString());
    metadata1.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 11).toString());
    metadata1.put(Vocabulary.SOURCE_SYSTEM, "DEMO");
    
    Map<String, String> metadata2 = new HashMap<>();
    metadata2.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 18).toString());
    metadata2.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 10).toString());
    metadata2.put(Vocabulary.SOURCE_SYSTEM, "DEMO");

    Map<String, String> metadata3 = new HashMap<>();
    metadata3.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 28).toString());
    metadata3.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 27).toString());
    metadata3.put(Vocabulary.SOURCE_SYSTEM, "DEMO");
    
    unicon.matthews.oneroster.Class class1
      = new unicon.matthews.oneroster.Class.Builder()
          .withSourcedId("demo-class-1")
          .withTitle("Introduction to Organic Chemistry")
          .withMetadata(metadata1)
          .withStatus(Status.active)
          .build();
    
    unicon.matthews.oneroster.Class class2
      = new unicon.matthews.oneroster.Class.Builder()
        .withSourcedId("demo-class-2")
        .withTitle("Advanced Chemistry 303")
        .withMetadata(metadata2)
        .withStatus(Status.active)
        .build();

    unicon.matthews.oneroster.Class class3
      = new unicon.matthews.oneroster.Class.Builder()
        .withSourcedId("demo-class-3")
        .withTitle("MicroBiology 201")
        .withMetadata(metadata3)
        .withStatus(Status.active)
        .build();
    
    classes.put("demo-class-1", class1);
    classes.put("demo-class-2", class2);
    classes.put("demo-class-3", class3);
    return classes.get(classSourcedId);
  }

}
