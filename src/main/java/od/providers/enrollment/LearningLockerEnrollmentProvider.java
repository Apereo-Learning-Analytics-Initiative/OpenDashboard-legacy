/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package od.providers.enrollment;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.course.learninglocker.LearningLockerModuleInstance;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.learninglocker.LearningLockerStaffModuleInstance;
import od.providers.learninglocker.LearningLockerStudentModuleInstance;
import od.repository.mongo.MongoTenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;


/**
 * @author ggilbert
 *
 */
@Component("roster_learninglocker")
public class LearningLockerEnrollmentProvider extends LearningLockerProvider implements EnrollmentProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LearningLockerEnrollmentProvider.class);

  private static final String KEY = "roster_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_ROSTER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
     
  @Autowired private MongoTenantRepository mongoTenantRepository;
  
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
  public Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, boolean activeOnly) throws ProviderException {
    
    Set<Enrollment> output = null;
       
    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
            
    String studentModuleInstanceUrl = buildUrl(baseUrl, STUDENT_MODULE_INSTANCE_URI);
    MultiValueMap<String, String> studentModuleInstanceParams = new LinkedMultiValueMap<String, String>();
    studentModuleInstanceParams.add("query", String.format("{\"MOD_INSTANCE_ID\":\"%s\"}", classSourcedId));
    studentModuleInstanceParams.add("populate", "[{\"path\":\"moduleInstance\",\"populate\":{\"path\":\"module\"}},{\"path\":\"student\"}]");
    URI studentModuleInstanceURI = buildUri(studentModuleInstanceUrl, studentModuleInstanceParams);
    log.debug(studentModuleInstanceURI.toString());
    
    LearningLockerStudentModuleInstance [] studentModuleInstances 
      = restTemplate.exchange(studentModuleInstanceURI, 
        HttpMethod.GET, headers, LearningLockerStudentModuleInstance[].class).getBody();
    
    if (studentModuleInstances != null && studentModuleInstances.length > 0) {
      output = new HashSet<>();
      for(LearningLockerStudentModuleInstance smi : studentModuleInstances) {
        output.add(toEnrollment(Role.student,
            toUser(Role.student, 
                smi.getStudent().getLastName(), 
                smi.getStudent().getFirstName(), 
                smi.getStudent().getId(), 
                smi.getStudent().getStudentId()),
            toClass(smi.getModuleInstance())));
      }
    }
    
    return output;

  }

  @Override
  public Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId, boolean activeOnly) throws ProviderException {
    Set<Enrollment> output = null;

    RestTemplate restTemplate = getRestTemplate(providerData);
    HttpEntity headers = new HttpEntity<>(createHeadersWithBasicAuth(providerData.findValueForKey("key"), providerData.findValueForKey("secret")));
    String baseUrl = providerData.findValueForKey("base_url");
    
    log.debug("staff id is {}", userSourcedId);
    
    String staffModuleInstanceUrl = buildUrl(baseUrl, STAFF_MODULE_INSTANCE_URI);
    MultiValueMap<String, String> staffModuleInstanceParams = new LinkedMultiValueMap<String, String>();
    staffModuleInstanceParams.add("query", String.format("{\"STAFF_ID\":\"%s\"}", userSourcedId));
    staffModuleInstanceParams.add("populate", "[{\"path\":\"moduleInstance\",\"populate\":{\"path\":\"module\"}},{\"path\":\"staff\"}]");
    
    log.debug(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams).toString());
    
    LearningLockerStaffModuleInstance [] staffModuleInstances 
      = restTemplate.exchange(buildUri(staffModuleInstanceUrl, staffModuleInstanceParams), 
        HttpMethod.GET, headers, LearningLockerStaffModuleInstance[].class).getBody();
      
    if (staffModuleInstances != null && staffModuleInstances.length > 0) {
      output = new HashSet<>();
      for(LearningLockerStaffModuleInstance smi : staffModuleInstances) {
        output.add(toEnrollment(Role.teacher,
            toUser(Role.teacher, 
                smi.getStaff().getLastName(), 
                smi.getStaff().getFirstName(), 
                smi.getStaff().getId(), 
                smi.getStaff().getStaffId()),
            toClass(smi.getModuleInstance())));
      }
      
    }
    
    return output;
  }
  
  private unicon.matthews.oneroster.Class toClass(LearningLockerModuleInstance learningLockerModuleInstance) {
    if (learningLockerModuleInstance == null || learningLockerModuleInstance.getModule() == null) {
      throw new IllegalArgumentException();
    }
    
    unicon.matthews.oneroster.Class klass 
      = new unicon.matthews.oneroster.Class.Builder()
        .withSourcedId(learningLockerModuleInstance.getModInstanceId())
        .withTitle(learningLockerModuleInstance.getModule().getModName())
        .withStatus(Status.active)
        .build();
    
    return klass;
  }
  
  private User toUser(Role role, String lastName, String firstName, String sourcedId, String userId) {
    User user
    = new User.Builder()
      .withRole(role)
      .withFamilyName(lastName)
      .withGivenName(firstName)
      .withStatus(Status.active)
      .withSourcedId(sourcedId)
      .withUserId(userId)
      .build();

    return user;
  }

  private Enrollment toEnrollment(Role role, User user, unicon.matthews.oneroster.Class klass) {
    
    Enrollment enrollment
      = new Enrollment.Builder()
        .withRole(role)
        .withStatus(Status.active)
        .withUser(user)
        .withKlass(klass)
        .build();
        
    return enrollment;
//    Member member = new Member();
//    member.setId(student.getId());
//    member.setUser_id(student.getStudentId());
//    member.setRole(role);
//    
//    PersonImpl person = new PersonImpl();
//    
//    StringBuilder fullName = new StringBuilder();
//    
//    if (StringUtils.isNotBlank(student.getFirstName())) {
//      fullName.append(student.getFirstName()).append(" ");
//      person.setName_given(student.getFirstName());
//    }
//    else {
//      log.warn("{} has null or blank first name",student.getId());
//    }
//    
//    if (StringUtils.isNotBlank(student.getLastName())) {
//      fullName.append(student.getLastName());
//      person.setName_family(student.getLastName());
//    }
//    else {
//      log.warn("{} has null or blank last name",student.getId());
//    }
//    
//    if (fullName.length() > 0) {
//      person.setName_full(fullName.toString());
//    }
//    
//    person.setPhoto_url(student.getPhotoUrl());
//    
//    member.setPerson(person);
//    
//    return member;
  }

  public List<String> getUniqueUsersWithRole(ProviderData providerData, String role) throws ProviderException {
    throw new ProviderException("getUniqueTeacherIds not implemented");
  }
}
