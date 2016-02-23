/**
 * 
 */
package od.providers.course.learninglocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.api.PageWrapper;
import od.providers.course.CourseProvider;
import od.providers.learninglocker.LearningLockerProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@Component("courses_learninglocker")
public class LearningLockerCourseProvider extends LearningLockerProvider implements CourseProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LearningLockerCourseProvider.class);
  
  private static final String KEY = "courses_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_COURSE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
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

  private PageImpl<LearningLockerModuleInstance> fetch(Pageable pageable, String path) {
    
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
    ParameterizedTypeReference<PageWrapper<LearningLockerModuleInstance>> responseType = new ParameterizedTypeReference<PageWrapper<LearningLockerModuleInstance>>() {};
    
    PageWrapper<LearningLockerModuleInstance> pageWrapper = restTemplate.exchange(url, HttpMethod.GET, null, responseType).getBody();
    log.debug(pageWrapper.toString());
    List<LearningLockerModuleInstance> output;
    if (pageWrapper != null && pageWrapper.getContent() != null && !pageWrapper.getContent().isEmpty()) {
      output = new LinkedList<LearningLockerModuleInstance>(pageWrapper.getContent());
    }
    else {
      output = new ArrayList<LearningLockerModuleInstance>();
    }
    
    return new PageImpl<LearningLockerModuleInstance>(output, pageable, pageWrapper.getPage().getTotalElements());
  }


  @Override
  public Course getContext(ProviderOptions options) throws ProviderException {
    CourseImpl course = null;
    
    String path = "/api/v1/LearningLockerModuleInstances?query={\"_id\":\"%s\"}&populate=MODULE";
    path = String.format(path, options.getCourseId());
    
    Pageable pageable = new PageRequest(0, 1);    
    PageImpl<LearningLockerModuleInstance> page = fetch(pageable, path);
    
    if (page != null && page.hasContent()) {
      List<LearningLockerModuleInstance> LearningLockerModuleInstances = page.getContent();
      if (LearningLockerModuleInstances != null && !LearningLockerModuleInstances.isEmpty()) {
        LearningLockerModuleInstance learningLockerModuleInstance = LearningLockerModuleInstances.get(0);
        course = new CourseImpl();
        course.setId(options.getCourseId());
        course.setTitle(learningLockerModuleInstance.getModule().getModName());
      }
    }
    
    return course;
  }

  @Override
  public List<CourseImpl> getContexts(ProviderOptions options) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }

}
