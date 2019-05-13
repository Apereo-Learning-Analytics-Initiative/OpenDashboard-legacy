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
package od.providers.enrollment;

import java.util.List;
import java.util.Set;

import od.providers.Provider;
import od.providers.ProviderData;
import od.providers.ProviderException;
import unicon.matthews.oneroster.Enrollment;

/**
 * @author ggilbert
 *
 */
public interface EnrollmentProvider extends Provider {
	Set<Enrollment> getEnrollmentsForClass(ProviderData providerData, String classSourcedId, 
	    boolean activeOnly) throws ProviderException;
	Set<Enrollment> getEnrollmentsForUser(ProviderData providerData, String userSourcedId,
	    boolean activeOnly) throws ProviderException;
	 List<String> getUniqueUsersWithRole(ProviderData providerData, String role) throws ProviderException;
	
}
