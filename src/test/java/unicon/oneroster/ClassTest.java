/**
 * 
 */
package unicon.oneroster;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
public class ClassTest {
  
  @Test
  public void whenFullyPopulatedClassJsonContainsEverything() throws JsonProcessingException {
    
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
      
    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(klass);
    assertThat(result, containsString(testTitle));
  }
}
