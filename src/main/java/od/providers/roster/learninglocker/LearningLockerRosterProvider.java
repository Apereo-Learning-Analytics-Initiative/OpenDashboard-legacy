/**
 * 
 */
package od.providers.roster.learninglocker;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apereo.lai.Member;
import org.apereo.lai.Person;
import org.apereo.lai.impl.PersonImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;

import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.config.DefaultProviderConfiguration;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;
import od.providers.config.TranslatableKeyValueConfigurationOptions;
import od.providers.roster.RosterProvider;
import od.repository.ProviderDataRepositoryInterface;

/**
 * @author ggilbert
 *
 */
@Component("roster_learninglocker")
public class LearningLockerRosterProvider implements RosterProvider {

  private static final String KEY = "roster_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_ROSTER";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
  
  private RestTemplate restTemplate = new RestTemplate();
  @Autowired private ProviderDataRepositoryInterface providerDataRepositoryInterface;
  
  @PostConstruct
  public void init() {
    LinkedList<ProviderConfigurationOption> options = new LinkedList<ProviderConfigurationOption>();
    ProviderConfigurationOption key = new TranslatableKeyValueConfigurationOptions("key", null, ProviderConfigurationOption.TEXT_TYPE, true, "Key", "LABEL_KEY",  true);
    ProviderConfigurationOption secret = new TranslatableKeyValueConfigurationOptions("secret", null, ProviderConfigurationOption.PASSWORD_TYPE, true, "Secret", "LABEL_SECRET", true);
    ProviderConfigurationOption baseUrl = new TranslatableKeyValueConfigurationOptions("base_url", null, ProviderConfigurationOption.URL_TYPE, true, "Learning Locker Base URL", "LABEL_LEARNINGLOCKER_BASE_URL", false);
    options.add(key);
    options.add(secret);
    options.add(baseUrl);

    providerConfiguration = new DefaultProviderConfiguration(options);
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
  public ProviderConfiguration getProviderConfiguration() {
    return providerConfiguration;
  }

  @Override
  public Set<Member> getRoster(ProviderOptions options) throws ProviderException {
    Set<Member> members = new HashSet<Member>();
    
    Member member1 = new Member();
    member1.setId(UUID.randomUUID().toString());
    member1.setUser_id("4");
    member1.setRole("Learner");
    PersonImpl person1 = new PersonImpl();
    person1.setName_full("James Pedroia");
    person1.setContact_email_primary("jp@test.com");
    person1.setName_given("James");
    person1.setName_given("Pedroia");
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
    person2.setName_given("Wales");
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
    person3.setName_given("Walker");
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
    person4.setName_given("McBride");
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
    person5.setName_given("Hunting");
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
    person6.setName_given("Hunting");
    member6.setPerson(person6);
    members.add(member6);
    
    return members;
  }

}
