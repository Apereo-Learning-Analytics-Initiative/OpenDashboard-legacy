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
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.providers.enrollment.EnrollmentProvider;
import od.providers.events.EventProvider;
import od.providers.modeloutput.ModelOutputProvider;

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
  
  @Autowired private Map<String, CourseProvider> courseProviders;
  @Autowired private Map<String, EventProvider> eventProviders;
  @Autowired private Map<String, ModelOutputProvider> modelOutputProviders;
  @Autowired private Map<String, EnrollmentProvider> rosterProviders;
    
  public List<Provider> getProvidersByType(final String type) {
    if (StringUtils.isBlank(type)) {
      throw new IllegalArgumentException("Provider type cannot be null");
    }
    
    List<Provider> providers = null;
    
    // TODO figure out a way to make this more dynamic
    if (COURSE.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(courseProviders.values());
    }
    else if (EVENT.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(eventProviders.values());
    }
    else if (MODELOUTPUT.equalsIgnoreCase(type)) {
      providers = new ArrayList<Provider>(modelOutputProviders.values());
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
    if (COURSE.equalsIgnoreCase(type)) {
      provider = courseProviders.get(key);
    }
    else if (EVENT.equalsIgnoreCase(type)) {
      provider = eventProviders.get(key);
    }
    else if (MODELOUTPUT.equalsIgnoreCase(type)) {
      provider = modelOutputProviders.get(key);
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
  
  public CourseProvider getCourseProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, COURSE);
    return courseProviders.get(pd.getProviderKey());
  }
  
  public EventProvider getEventProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, EVENT);
    return eventProviders.get(pd.getProviderKey());
  }

  public ModelOutputProvider getModelOutputProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, MODELOUTPUT);
    return modelOutputProviders.get(pd.getProviderKey());
  }
  
  public EnrollmentProvider getRosterProvider(Tenant tenant) throws ProviderDataConfigurationException {
    ProviderData pd = getConfiguredProviderDataByType(tenant, ROSTER);
    return rosterProviders.get(pd.getProviderKey());
  }

  public Map<String, CourseProvider> getCourseProviders() {
    return courseProviders;
  }

  public Map<String, EventProvider> getEventProviders() {
    return eventProviders;
  }

  public Map<String, ModelOutputProvider> getModelOutputProviders() {
    return modelOutputProviders;
  }

  public Map<String, EnrollmentProvider> getRosterProviders() {
    return rosterProviders;
  }
  

}
