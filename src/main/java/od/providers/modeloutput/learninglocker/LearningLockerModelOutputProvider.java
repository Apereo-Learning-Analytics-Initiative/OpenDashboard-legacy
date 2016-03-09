/**
 * 
 */
package od.providers.modeloutput.learninglocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apereo.lai.ModelOutput;
import org.apereo.lai.impl.ModelOutputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@Component("modeloutput_learninglocker")
public class LearningLockerModelOutputProvider extends LearningLockerProvider implements ModelOutputProvider {

  private static final Logger log = LoggerFactory.getLogger(LearningLockerModelOutputProvider.class);

  private static final String KEY = "modeloutput_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_MODELOUTPUT";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  private boolean DEMO = true;
  
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;
  
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultLearningLockerConfiguration();
  }

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
  public Page<ModelOutput> getModelOutputForCourse(ProviderOptions options, String tenant, String course, Pageable page) throws ProviderException {
    if (DEMO) {
      return demoData();
    }
    return null;
  }

  @Override
  public Page<ModelOutput> getModelOutputForStudent(ProviderOptions options, String tenant, String student, Pageable page) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }
  
  private Page<ModelOutput> demoData() {

  String json = 
      "[{\"output\":{\"RMN_SCORE_PARTIAL\":\"82.6232\",\"RC_FINAL_GRADE\":\"1.3\",\"R_ASSMT_SUB\":null,\"COURSE_ID\":\"13\","
      + "\"PERCENTILE\":0,\"SAT_MATH\":650,\"ENROLLMENT\":5,\"SAT_VERBAL\":600,\"R_CONTENT_READ\":\"1.0\",\"ACADEMIC_RISK\":1,"
      + "\"APTITUDE_SCORE\":1190,\"R_ASN_SUB\":\"1.0\",\"ID\":1,\"MODEL_RISK_CONFIDENCE\":\"LOW RISK\",\"R_ASN_READ\":null,"
      + "\"AGE\":22,\"ONLINE_FLAG\":false,\"R_FORUM_POST\":null,\"R_LESSONS_VIEW\":null,\"FAIL_PROBABILITY\":\"0.596\","
      + "\"PASS_PROBABILITY\":\"0.404\",\"ALTERNATIVE_ID\":\"8\",\"RC_GENDER\":2,\"GPA_CUMULATIVE\":\"2.2182\",\"RC_CLASS_CODE\":\"4\","
      + "\"STANDING\":\"0\",\"GPA_SEMESTER\":\"2.6600\",\"R_FORUM_READ\":null,\"RC_ENROLLMENT_STATUS\":null,\"SUBJECT\":\"Analysis\",\"R_SESSIONS\":"
      + "\"1.5663\",\"R_ASSMT_TAKE\":null,\"RMN_SCORE\":\"82.6232\"},\"createdDate\":1446481125659},  "
      + "{\"output\":{\"RMN_SCORE_PARTIAL\":\"125.0927\",\"RC_FINAL_GRADE\":\"3.7\",\"R_ASSMT_SUB\":null,\"COURSE_ID\":\"13\",\"PERCENTILE\":94,"
      + "\"SAT_MATH\":660,\"ENROLLMENT\":5,\"SAT_VERBAL\":620,\"R_CONTENT_READ\":\"2.2\",\"ACADEMIC_RISK\":2,\"APTITUDE_SCORE\":1280,"
      + "\"R_ASN_SUB\":null,\"ID\":2,\"MODEL_RISK_CONFIDENCE\":\"NO RISK\",\"R_ASN_READ\":null,\"AGE\":22,\"ONLINE_FLAG\":false,"
      + "\"R_FORUM_POST\":null,\"R_LESSONS_VIEW\":null,\"FAIL_PROBABILITY\":\"0.000\",\"PASS_PROBABILITY\":\"1.000\","
      + "\"ALTERNATIVE_ID\":\"4\",\"RC_GENDER\":2,\"GPA_CUMULATIVE\":\"3.8729\",\"RC_CLASS_CODE\":\"1\",\"STANDING\":\"2\","
      + "\"GPA_SEMESTER\":\"3.6800\",\"R_FORUM_READ\":null,\"RC_ENROLLMENT_STATUS\":null,\"SUBJECT\":\"Analysis\","
      + "\"R_SESSIONS\":\"1.3253\",\"R_ASSMT_TAKE\":null,\"RMN_SCORE\":\"125.0927\"},\"createdDate\":1446481125668},  "
      + "{\"output\":{\"RMN_SCORE_PARTIAL\":\"121.4125\",\"RC_FINAL_GRADE\":\"3.3\",\"R_ASSMT_SUB\":null,\"COURSE_ID\":\"13\","
      + "\"PERCENTILE\":92,\"SAT_MATH\":590,\"ENROLLMENT\":5,\"SAT_VERBAL\":580,\"R_CONTENT_READ\":\"1.0\",\"ACADEMIC_RISK\":2,"
      + "\"APTITUDE_SCORE\":1170,\"R_ASN_SUB\":null,\"ID\":3,\"MODEL_RISK_CONFIDENCE\":\"NO RISK\",\"R_ASN_READ\":null,"
      + "\"AGE\":21,\"ONLINE_FLAG\":false,\"R_FORUM_POST\":null,\"R_LESSONS_VIEW\":null,\"FAIL_PROBABILITY\":\"0.000\","
      + "\"PASS_PROBABILITY\":\"1.000\",\"ALTERNATIVE_ID\":\"6\",\"RC_GENDER\":1,\"GPA_CUMULATIVE\":\"3.5438\","
      + "\"RC_CLASS_CODE\":\"4\",\"STANDING\":\"2\",\"GPA_SEMESTER\":\"3.6500\",\"R_FORUM_READ\":null,\"RC_ENROLLMENT_STATUS\":null,"
      + "\"SUBJECT\":\"Analysis\",\"R_SESSIONS\":\"1.0643\",\"R_ASSMT_TAKE\":null,\"RMN_SCORE\":\"121.4125\"},\"createdDate\":1446481125672},  "
      + "{\"output\":{\"RMN_SCORE_PARTIAL\":\"93.6217\",\"RC_FINAL_GRADE\":\"2.0\",\"R_ASSMT_SUB\":null,\"COURSE_ID\":\"13\",\"PERCENTILE\":87,"
      + "\"SAT_MATH\":600,\"ENROLLMENT\":5,\"SAT_VERBAL\":570,\"R_CONTENT_READ\":\"0.6\",\"ACADEMIC_RISK\":2,\"APTITUDE_SCORE\":1170,\"R_ASN_SUB\":null,"
      + "\"ID\":4,\"MODEL_RISK_CONFIDENCE\":\"NO RISK\",\"R_ASN_READ\":null,\"AGE\":22,\"ONLINE_FLAG\":false,\"R_FORUM_POST\":null,\"R_LESSONS_VIEW\":null,"
      + "\"FAIL_PROBABILITY\":\"0.272\",\"PASS_PROBABILITY\":\"0.728\",\"ALTERNATIVE_ID\":\"5\",\"RC_GENDER\":2,\"GPA_CUMULATIVE\":\"2.5000\","
      + "\"RC_CLASS_CODE\":\"3\",\"STANDING\":\"0\",\"GPA_SEMESTER\":\"2.3400\",\"R_FORUM_READ\":null,\"RC_ENROLLMENT_STATUS\":null,\"SUBJECT\":\"Analysis\","
      + "\"R_SESSIONS\":\"0.8434\",\"R_ASSMT_TAKE\":null,\"RMN_SCORE\":\"93.6217\"},\"createdDate\":1446481125673},  "
      + "{\"output\":{\"RMN_SCORE_PARTIAL\":\"77.2500\",\"RC_FINAL_GRADE\":\"1.0\",\"R_ASSMT_SUB\":null,\"COURSE_ID\":\"13\","
      + "\"PERCENTILE\":0,\"SAT_MATH\":0,\"ENROLLMENT\":5,\"SAT_VERBAL\":0,\"R_CONTENT_READ\":\"0.3\",\"ACADEMIC_RISK\":1,\"APTITUDE_SCORE\":0,"
      + "\"R_ASN_SUB\":null,\"ID\":5,\"MODEL_RISK_CONFIDENCE\":\"MEDIUM RISK\",\"R_ASN_READ\":null,\"AGE\":36,\"ONLINE_FLAG\":false,\"R_FORUM_POST\":null,"
      + "\"R_LESSONS_VIEW\":null,\"FAIL_PROBABILITY\":\"0.884\",\"PASS_PROBABILITY\":\"0.116\",\"ALTERNATIVE_ID\":\"7\",\"RC_GENDER\":1,\"GPA_CUMULATIVE\":\"2.3350\","
      + "\"RC_CLASS_CODE\":\"4\",\"STANDING\":\"0\",\"GPA_SEMESTER\":\"1.7667\",\"R_FORUM_READ\":null,\"RC_ENROLLMENT_STATUS\":null,"
      + "\"SUBJECT\":\"Analysis\",\"R_SESSIONS\":\"0.2008\",\"R_ASSMT_TAKE\":null,\"RMN_SCORE\":\"77.2500\"},\"createdDate\":1446481125674}]";
  
    try {
      ObjectMapper om = new ObjectMapper();
      ModelOutput[] output = om.readValue(json, new TypeReference<ModelOutputImpl[]>() {});
        
      if (output != null && output.length != 0) {
        return new PageImpl(Arrays.asList(output));
      }
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new PageImpl(new ArrayList<>());
  }

}
