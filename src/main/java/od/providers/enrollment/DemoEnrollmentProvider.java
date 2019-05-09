/**
 * 
 */
package od.providers.enrollment;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;
import unicon.oneroster.Vocabulary;

/**
 * @author ggilbert
 *
 */
@Component("roster_demo")
public class DemoEnrollmentProvider implements EnrollmentProvider {

  private static final Logger log = LoggerFactory.getLogger(DemoEnrollmentProvider.class);
  
  private static final String KEY = "roster_demo";
  private static final String BASE = "OD_DEMO_ENROLLMENTS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  private Map<String, Set<Enrollment>> studentEnrollments;
  private Set<Enrollment> staffEnrollments;
  
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
  public void init() {
    staffEnrollments = new HashSet<>();
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

    User teacher 
      = new User.Builder()
        .withSourcedId("teacher-sourcedId-1")
        .withRole(Role.teacher)
        .withFamilyName("Wooden")
        .withGivenName("John")
        .withUserId("teacher-userid-1")
        .withStatus(Status.active)
        .build();
    
    Enrollment teacherEnrollment1
      = new Enrollment.Builder()
          .withKlass(class1)
          .withUser(teacher)
          .withPrimary(true)
          .withSourcedId("teacher-enrollment-1")
          .withRole(Role.teacher)
          .withStatus(Status.active)
          .build();
    
    staffEnrollments.add(teacherEnrollment1);
    
    Enrollment teacherEnrollment2
    = new Enrollment.Builder()
        .withKlass(class2)
        .withUser(teacher)
        .withPrimary(true)
        .withSourcedId("teacher-enrollment-2")
        .withRole(Role.teacher)
        .withStatus(Status.active)
        .build();
  
    staffEnrollments.add(teacherEnrollment2);

    Enrollment teacherEnrollment3
    = new Enrollment.Builder()
        .withKlass(class3)
        .withUser(teacher)
        .withPrimary(true)
        .withSourcedId("teacher-enrollment-3")
        .withRole(Role.teacher)
        .withStatus(Status.active)
        .build();
  
    staffEnrollments.add(teacherEnrollment3);
    
    studentEnrollments = new HashMap<>();
    Set<Enrollment> class1Enrollments = new HashSet<>();
    Set<Enrollment> class2Enrollments = new HashSet<>();
    Set<Enrollment> class3Enrollments = new HashSet<>();
    studentEnrollments.put("demo-class-1", class1Enrollments);
    studentEnrollments.put("demo-class-2", class2Enrollments);
    studentEnrollments.put("demo-class-3", class3Enrollments);
    
    String [] fn = {"Mark", "Kate", "Gary", "Steve", "Lucas", "Wyatt", "Ali", "Jessica", "Catherine",
        "Philip", "Pedro", "P.J.", "Nicole", "Eliza", "James", "Kristen", "Xander", "Mookie", "Eddie", "Kara", "Ella", "Ruth",
        "Josh", "Emma", "Matthew", "David", "Jean", "Tom", "Raymond"};

    String [] ln = {"Gilbert", "Ficus", "Smith", "Wesson", "Johnstone", "Ortiz", "Jones", "LaMarche",
        "Gauvin", "Betts", "Brady", "Ciruso", "Elliot", "Bird", "Garciaparra", "Thomas", "Donnelly", "Donovan"};

    
    for (int s = 0; s < 60; s++) {
      String studentSourcedId = "demo-student-".concat(String.valueOf(s));
      
      User student 
      = new User.Builder()
        .withSourcedId(studentSourcedId)
        .withRole(Role.student)
        .withFamilyName(ln[ThreadLocalRandom.current().nextInt(0, ln.length)])
        .withGivenName(fn[ThreadLocalRandom.current().nextInt(0, fn.length)])
        .withUserId(studentSourcedId)
        .withStatus(Status.active)
        .build();
      
      Enrollment studentEnrollment1
      = new Enrollment.Builder()
          .withKlass(class1)
          .withUser(student)
          .withPrimary(false)
          .withSourcedId("student-enrollment-c1-"+s)
          .withRole(Role.student)
          .withStatus(Status.active)
          .build();
      
      class1Enrollments.add(studentEnrollment1);
      
    
      Enrollment studentEnrollment2
      = new Enrollment.Builder()
          .withKlass(class2)
          .withUser(student)
          .withPrimary(false)
          .withSourcedId("student-enrollment-c2-"+s)
          .withRole(Role.student)
          .withStatus(Status.active)
          .build();
    
      class2Enrollments.add(studentEnrollment2);
  
      Enrollment studentEnrollment3
      = new Enrollment.Builder()
          .withKlass(class3)
          .withUser(student)
          .withPrimary(false)
          .withSourcedId("student-enrollment-c3-"+s)
          .withRole(Role.student)
          .withStatus(Status.active)
          .build();

      class3Enrollments.add(studentEnrollment3);
    }
  }

  @Override
  public Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, boolean activeOnly) throws ProviderException {
     return studentEnrollments.get(classSourcedId);
  }

  @Override
  public Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId, boolean activeOnly) throws ProviderException {
    return staffEnrollments;  
  }
  
  @Override
  public List<String> getUniqueUsersWithRole(ProviderData providerData, String role) throws ProviderException {
    throw new ProviderException("getUniqueTeacherIds not implemented");
  }
  

}
