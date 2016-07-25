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
package od.providers.assignment.sakai;

import java.util.List;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.assignment.AssignmentsProvider;
import od.providers.config.ProviderConfiguration;
import od.providers.sakai.BaseSakaiProvider;

import org.apache.commons.lang3.StringUtils;
import org.apereo.lai.impl.AssignmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author mflynn
 *
 */
@Component("assignments_sakai")
public class SakaiAssignmentsProvider extends BaseSakaiProvider implements AssignmentsProvider {

  private static final Logger log = LoggerFactory.getLogger(SakaiAssignmentsProvider.class);
  private final String COLLECTION_URI = "/direct/assignment/site/{ID}.json";
  
  private static final String KEY = "assignments_sakai";
  private static final String BASE = "SAKAI_ASSIGNMENT_WEB_SERVICE";
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
  public List<AssignmentImpl> getAssignments(ProviderData providerData, String contextId) {
    
    String url = fullUrl(providerData, StringUtils.replace(COLLECTION_URI, "{ID}", contextId));
    ResponseEntity<SakaiAssignmentCollection> messageResponse = restTemplate.getForEntity(url + "?_sessionId=" + getSakaiSession(providerData), SakaiAssignmentCollection.class);
    return messageResponse.getBody().getAssignment_collection();
  }



}
