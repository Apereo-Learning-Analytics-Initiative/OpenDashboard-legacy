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
package od.providers.outcomes.learninglocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderOptions;
import od.providers.learninglocker.LearningLockerProvider;
import od.providers.outcomes.OutcomesProvider;
import od.repository.ProviderDataRepositoryInterface;

import org.apereo.lai.impl.LineItemImpl;
import org.apereo.lai.impl.ResultImpl;
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
@Component("outcomes_learninglocker")
public class LearningLockerOutcomesProvider extends LearningLockerProvider implements OutcomesProvider {
  
  private static final Logger log = LoggerFactory.getLogger(LearningLockerOutcomesProvider.class);

  private static final String KEY = "outcomes_learninglocker";
  private static final String BASE = "LEARNING_LOCKER_OUTCOMES";
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

  @Override
  public List<LineItemImpl> getOutcomesForCourse(ProviderOptions options) throws ProviderException {
    String path = "/api/v1/grades?query={\"MODULE_INSTANCE\":\"%s\"}";
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
    
    LearningLockerGrade [] grades = restTemplate.exchange(url, HttpMethod.GET, null, LearningLockerGrade[].class).getBody();
    List<LineItemImpl> output;
    if (grades != null && grades.length > 0) {
      output = new ArrayList<LineItemImpl>();
      Map<String, LineItemImpl> lineItemsMap = new HashMap<String, LineItemImpl>();
      for (LearningLockerGrade grade : grades) {
        
        LineItemImpl lineItem = toLineItem(grade);
        ResultImpl result = toResult(grade);
        
        LineItemImpl existingLineItem = lineItemsMap.get(grade.getGradableObject());
        if (existingLineItem != null) {
          existingLineItem.getResults().add(result);
        }
        else {
          List<ResultImpl> results = new ArrayList<ResultImpl>();
          results.add(result);
          lineItem.setResults(results);
          lineItemsMap.put(grade.getGradableObject(),lineItem);
        }
      }
      
      output.addAll(lineItemsMap.values());
    }
    else {
      output = new ArrayList<LineItemImpl>();
    }
    
    return output;
  }
  
  public LineItemImpl toLineItem(LearningLockerGrade grade) {
    LineItemImpl lineItem = new LineItemImpl();
    lineItem.setContext(grade.getModuleInstanceId());
    lineItem.setMaximumScore(new Double(grade.getMaxPoints()));
    lineItem.setTitle(grade.getGradableObject());
    return lineItem;
  }
  
  public ResultImpl toResult(LearningLockerGrade grade) {
    ResultImpl result = new ResultImpl();
    result.setUserId(grade.getStudentId());
    result.setGrade(String.valueOf(grade.getEarnedPoints()));
    result.setId(grade.getId());
    return result;
  }

}
