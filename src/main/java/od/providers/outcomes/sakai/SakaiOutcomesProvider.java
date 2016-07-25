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
/**
 * 
 */
package od.providers.outcomes.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.outcomes.OutcomesProvider;
import od.providers.sakai.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.impl.LineItemImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component("outcomes_sakai")
public class SakaiOutcomesProvider extends BaseSakaiProvider implements OutcomesProvider {
  
  private final String ENTITY_URI = "/direct/grades/gradebook/{ID}.json";
  
  private static final String KEY = "outcomes_sakai";
  private static final String BASE = "SAKAI_OUTCOMES_WEB_SERVICE";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  private ProviderConfiguration providerConfiguration;
 
  @PostConstruct
  public void init() {
    providerConfiguration = getDefaultSakaiProviderConfiguration();
  }

  @Override
  public List<LineItemImpl> getOutcomesForCourse(ProviderData providerData, String courseId) throws ProviderException {

    List<LineItemImpl> lineItems = null;
    
    String url = fullUrl(providerData, StringUtils.replace(ENTITY_URI, "{ID}", courseId));
    ResponseEntity<SakaiGradebook> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiGradebook.class);
    
    if (messageResponse != null) {
      SakaiGradebook sakaiGradebook = messageResponse.getBody();
      lineItems = sakaiGradebook.toLineItems();
    }
    
    return lineItems;
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

}
