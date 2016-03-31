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
package od.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import od.framework.model.Tenant;
import od.providers.assignment.AssignmentsProvider;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.events.EventProvider;
import od.providers.forum.ForumsProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.providers.outcomes.OutcomesProvider;
import od.providers.roster.RosterProvider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class ProviderService {
  
  public static final String ASSIGNMENT = "ASSIGNMENT";
  public static final String COURSE = "COURSE";
  public static final String EVENT = "EVENT";
  public static final String FORUM = "FORUM";
  public static final String MODELOUTPUT = "MODELOUTPUT";
  public static final String OUTCOMES = "OUTCOME";
  public static final String ROSTER = "ROSTER";
  
  @Autowired private Map<String, AssignmentsProvider> assignmentProviders;
  @Autowired private Map<String, CourseProvider> courseProviders;
  @Autowired private Map<String, EventProvider> eventProviders;
  @Autowired private Map<String, ForumsProvider> forumsProviders;
  @Autowired private Map<String, ModelOutputProvider> modelOutputProviders;
  @Autowired private Map<String, OutcomesProvider> outcomesProviders;
  @Autowired private Map<String, RosterProvider> rosterProviders;
    
  public List<Provider> getProvidersByType(final String type) {
    if (StringUtils.isBlank(type)) {
      throw new IllegalArgumentException("Provider type cannot be null");
    }
    
    List<Provider> providers = null;
    
    // TODO figure out a way to make this more dynamic
    if (ASSIGNMENT.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(assignmentProviders.values());
    }
    else if (COURSE.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(courseProviders.values());
    }
    else if (EVENT.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(eventProviders.values());
    }
    else if (FORUM.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(forumsProviders.values());
    }
    else if (MODELOUTPUT.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(modelOutputProviders.values());
    }
    else if (OUTCOMES.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(outcomesProviders.values());
    }
    else if (ROSTER.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(rosterProviders.values());
    }
    
    return providers;
  }
  
  public Provider getProviderByTypeAndKey(final String type, final String key) {
    if (StringUtils.isBlank(type) || StringUtils.isBlank(key)) {
      throw new IllegalArgumentException("Provider type or key cannot be null");
    }
    
    Provider provider = null;
    
    // TODO figure out a way to make this more dynamic
    if (ASSIGNMENT.equalsIgnoreCase(type)) {
      provider = assignmentProviders.get(key);
    }
    else if (COURSE.equalsIgnoreCase(type)) {
      provider = courseProviders.get(key);
    }
    else if (EVENT.equalsIgnoreCase(type)) {
      provider = eventProviders.get(key);
    }
    else if (FORUM.equalsIgnoreCase(type)) {
      provider = forumsProviders.get(key);
    }
    else if (MODELOUTPUT.equalsIgnoreCase(type)) {
      provider = modelOutputProviders.get(key);
    }
    else if (OUTCOMES.equalsIgnoreCase(type)) {
      provider = outcomesProviders.get(key);
    }
    else if (ROSTER.equalsIgnoreCase(type)) {
      provider = rosterProviders.get(key);
    }
    
    return provider;
  }
  
  public ProviderData getConfiguredProviderDataByType(Tenant tenant, String type) throws ProviderDataConfigurationException {
    List<ProviderData> providerDataList = tenant.findByType(type);
    if (providerDataList != null && !providerDataList.isEmpty()) {
      if (providerDataList.size() == 1) {
        return providerDataList.get(0);
      }
      else {
        throw new ProviderDataConfigurationException("Multiple providers configured for type: "+type);
      }
    }
    else {
      throw new ProviderDataConfigurationException("No providers configured for type: "+type);
    }
  }
  
  public AssignmentsProvider getAssignmentsProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, ASSIGNMENT);
    return assignmentProviders.get(pd.getProviderKey());
  }
  
  public AssignmentsProvider getAssignmentsProvider(String key) {
    return assignmentProviders.get(key);
  }
  
  public CourseProvider getCourseProvider(String key) {
    return courseProviders.get(key);
  }
  
  public CourseProvider getCourseProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, COURSE);
    return courseProviders.get(pd.getProviderKey());
  }
  
  public EventProvider getEventProvider(String key) {
    return eventProviders.get(key);
  }
  
  public EventProvider getEventProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, EVENT);
    return eventProviders.get(pd.getProviderKey());
  }

  public ForumsProvider getForumsProvider(String key) {
    return forumsProviders.get(key);
  }
  
  public ForumsProvider getForumsProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, FORUM);
    return forumsProviders.get(pd.getProviderKey());
  }

  public ModelOutputProvider getModelOutputProvider(String key) {
    return modelOutputProviders.get(key);
  }
  
  public ModelOutputProvider getModelOutputProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, MODELOUTPUT);
    return modelOutputProviders.get(pd.getProviderKey());
  }
  
  public OutcomesProvider getOutcomesProvider(String key) {
    return outcomesProviders.get(key);
  }
  
  public OutcomesProvider getOutcomesProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, OUTCOMES);
    return outcomesProviders.get(pd.getProviderKey());
  }
  
  public RosterProvider getRosterProvider(String key) {
    return rosterProviders.get(key);
  }
  
  public RosterProvider getRosterProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, ROSTER);
    return rosterProviders.get(pd.getProviderKey());
  }

  public Map<String, AssignmentsProvider> getAssignmentProviders() {
    return assignmentProviders;
  }

  public Map<String, CourseProvider> getCourseProviders() {
    return courseProviders;
  }

  public Map<String, EventProvider> getEventProviders() {
    return eventProviders;
  }

  public Map<String, ForumsProvider> getForumsProviders() {
    return forumsProviders;
  }

  public Map<String, ModelOutputProvider> getModelOutputProviders() {
    return modelOutputProviders;
  }

  public Map<String, OutcomesProvider> getOutcomesProviders() {
    return outcomesProviders;
  }

  public Map<String, RosterProvider> getRosterProviders() {
    return rosterProviders;
  }
  

}
