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
package od.providers.events;

import od.exception.MethodNotImplementedException;
import od.providers.Provider;
import od.providers.ProviderException;

import org.apereo.openlrs.model.event.v2.ClassEventStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ggilbert
 *
 */
public interface EventProvider extends Provider {
  
  ClassEventStatistics getStatisticsForClass(String tenantId, String classSourcedId, boolean studentsOnly) throws ProviderException;
  
  Page<org.apereo.lai.Event> getEventsForUser(String tenantId, String userId, Pageable pageable) throws ProviderException;
  Page<org.apereo.lai.Event> getEventsForCourse(String tenantId, String courseId, Pageable pageable) throws ProviderException;
  Page<org.apereo.lai.Event> getEventsForCourseAndUser(String tenantId, String courseId, String userId, Pageable pageable) throws ProviderException;
  JsonNode postEvent(JsonNode marshallableObject, String tenantId) throws ProviderException, MethodNotImplementedException;
}
