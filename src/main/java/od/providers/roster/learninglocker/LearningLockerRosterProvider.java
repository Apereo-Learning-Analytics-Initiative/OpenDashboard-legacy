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
package od.providers.roster.learninglocker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.learninglocker.LearningLockerStudent;
import od.providers.roster.RosterProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Member;
import org.apereo.lai.impl.PersonImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("roster_learninglocker")
public class LearningLockerRosterProvider extends LearningLockerProvider implements RosterProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LearningLockerRosterProvider.class);

  private static final String KEY = "roster_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_ROSTER";
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

  
  private Set<Member> demo() {
    Set<Member> members = new HashSet<>();
    
    Member member1 = new Member();
    member1.setId(UUID.randomUUID().toString());
    member1.setUser_id("4");
    member1.setRole("Learner");
    PersonImpl person1 = new PersonImpl();
    person1.setName_full("James Pedroia");
    person1.setContact_email_primary("jp@test.com");
    person1.setName_given("James");
    person1.setName_family("Pedroia");
    member1.setPerson(person1);
    members.add(member1);

    Member member2 = new Member();
    member2.setId(UUID.randomUUID().toString());
    member2.setUser_id("6");
    member2.setRole("Learner");
    PersonImpl person2 = new PersonImpl();
    person2.setName_full("Josie Wales");
    person2.setContact_email_primary("jwales@test.com");
    person2.setName_given("Josie");
    person2.setName_family("Wales");
    member2.setPerson(person2);
    members.add(member2);

    Member member3 = new Member();
    member3.setId(UUID.randomUUID().toString());
    member3.setUser_id("5");
    member3.setRole("Learner");
    PersonImpl person3 = new PersonImpl();
    person3.setName_full("Luke Walker");
    person3.setContact_email_primary("lw@test.com");
    person3.setName_given("Luke");
    person3.setName_family("Walker");
    member3.setPerson(person3);
    members.add(member3);

    Member member4 = new Member();
    member4.setId(UUID.randomUUID().toString());
    member4.setUser_id("9");
    member4.setRole("Instructor");
    PersonImpl person4 = new PersonImpl();
    person4.setName_full("Sean McBride");
    person4.setContact_email_primary("sean@teachers.com");
    person4.setName_given("Sean");
    person4.setName_family("McBride");
    member4.setPerson(person4);
    members.add(member4);

    Member member5 = new Member();
    member5.setId(UUID.randomUUID().toString());
    member5.setUser_id("7");
    member5.setRole("Learner");
    PersonImpl person5 = new PersonImpl();
    person5.setName_full("Skylar Hunting");
    person5.setContact_email_primary("shunting@test.com");
    person5.setName_given("Skylar");
    person5.setName_family("Hunting");
    member5.setPerson(person5);
    members.add(member5);

    Member member6 = new Member();
    member6.setId(UUID.randomUUID().toString());
    member6.setUser_id("8");
    member6.setRole("Learner");
    PersonImpl person6 = new PersonImpl();
    person6.setName_full("Will Hunting");
    person6.setContact_email_primary("whunting@test.com");
    person6.setName_given("Will Hunting");
    person6.setName_family("Hunting");
    member6.setPerson(person6);
    members.add(member6);
    
    return members;

  }

  @Override
  public Set<Member> getRoster(ProviderOptions options) throws ProviderException {
    if (DEMO) {
      return demo();
    }
    
    String path = "/api/v1/students?query={\"MODULE_INSTANCES\":\"%s\"}";
    path = String.format(path, options.getCourseId());
    
    log.debug("{}",path);
    
    ProviderData providerData = providerDataRepositoryInterface.findByProviderKey(KEY);

    String url = providerData.findValueForKey("base_url");
    if (!url.endsWith("/") && !path.startsWith("/")) {
      url = url + "/";
    }
    
    url = url + path;
    
    ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
    resourceDetails.setClientId(providerData.findValueForKey("key"));
    resourceDetails.setClientSecret(providerData.findValueForKey("secret"));
    resourceDetails.setAccessTokenUri(getUrl(providerData.findValueForKey("base_url"), LL_OAUTH_TOKEN_URI, null));
    DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript")));
    restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(converter));
    
    LearningLockerStudent [] students = restTemplate.exchange(url, HttpMethod.GET, null, LearningLockerStudent[].class).getBody();
    Set<Member> output = null;
    if (students != null && students.length > 0) {
      output = new HashSet<>();
      
      for (LearningLockerStudent student : students) {
        output.add(toMember("Learner",student));
      }
    }
    else {
      output = new HashSet<>();
    }
    
    return output;

  }
  
  private Member toMember(String role, LearningLockerStudent student) {
    Member member = new Member();
    member.setId(student.getId());
    member.setUser_id(student.getStudentId());
    member.setRole(role);
    
    PersonImpl person = new PersonImpl();
    StringBuilder fullName = new StringBuilder();
    
    if (StringUtils.isNotBlank(student.getFirstName())) {
      fullName.append(student.getFirstName()).append(" ");
      person.setName_given(student.getFirstName());
    }
    
    if (StringUtils.isNotBlank(student.getLastName())) {
      fullName.append(student.getLastName());
      person.setName_family(student.getLastName());
    }
    
    if (fullName.length() > 0) {
      person.setName_full(fullName.toString());
    }
    
    person.setPhoto_url(student.getPhotoUrl());
    
    member.setPerson(person);
    
    return member;
  }

}
