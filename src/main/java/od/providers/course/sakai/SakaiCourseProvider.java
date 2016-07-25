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
package od.providers.course.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.course.CourseProvider;
import od.providers.sakai.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("courses_sakai")
public class SakaiCourseProvider extends BaseSakaiProvider implements CourseProvider {
  
  private final String COLLECTION_URI = "/direct/site.json";
  private final String ENTITY_URI ="/direct/site/{ID}.json";
  
  private static final String KEY = "courses_sakai";
  private static final String BASE = "SAKAI_COURSES_WEB_SERVICE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;

  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultSakaiProviderConfiguration();
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
  public Course getContext(ProviderData providerData, String contextId) throws ProviderException {
    String url = fullUrl(providerData, StringUtils.replace(ENTITY_URI, "{ID}", contextId));
    ResponseEntity<CourseImpl> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), CourseImpl.class);
    return messageResponse.getBody();
  }

  @Override
  public List<Course> getContexts(ProviderData providerData, String userId) throws ProviderException {

    String url = fullUrl(providerData, COLLECTION_URI);
    ResponseEntity<SakaiSiteCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiSiteCollection.class);
    //TODO
    //return messageResponse.getBody().getSite_collection();
    return null;
  }

  @Override
  public List<String> getCourseIdByLTIContextId(Tenant tenant, String ltiContextId) throws ProviderException {
    // TODO Auto-generated method stub
    return null;
  }
}


