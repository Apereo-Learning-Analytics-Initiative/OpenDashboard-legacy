/**
 * 
 */
package unicon.oneroster;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author ggilbert
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EnrollmentTest {
  
  @Test
  public void whenFullyPopulatedEnrollmentJsonContainsEverything() throws JsonProcessingException {
    
    String testUUID = UUID.randomUUID().toString();
    Map<String, String> testMetadata = Collections.singletonMap("meta", "data");
    Status testStatus = Status.active;
    String testTitle = "test";
    
    Class klass =
        new Class.Builder()
        .withSourcedId(testUUID)
        .withMetadata(testMetadata)
        .withStatus(testStatus)
        .withTitle(testTitle)
        .build();
    
    User user =
        new User.Builder()
        .withSourcedId(testUUID)
        .build();
    
    Enrollment enrollment = 
        new Enrollment.Builder()
        .withKlass(klass)
        .withUser(user)
        .build();
      
    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(enrollment);
    assertThat(result, containsString("class"));
  }

}
