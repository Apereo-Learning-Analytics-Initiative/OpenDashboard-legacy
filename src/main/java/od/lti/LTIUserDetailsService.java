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
package od.lti;

import lti.LaunchRequest;
import od.OpenDashboardUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ggilbert
 *
 */
@Service
public class LTIUserDetailsService implements UserDetailsService {

  final static Logger log = LoggerFactory.getLogger(LTIUserDetailsService.class);

  @Override
  public UserDetails loadUserByUsername(String ltiJson) throws UsernameNotFoundException {
    log.debug(ltiJson);
    LaunchRequest launchRequest = LaunchRequest.fromJSON(ltiJson);
    String role = null;
    if (LTIController.hasInstructorRole(null, launchRequest.getRoles())) {
      role = "ROLE_INSTRUCTOR";
    } else {
      role = "ROLE_STUDENT";
    }

    return new OpenDashboardUser(launchRequest.getUser_id(), launchRequest.getUser_id(), 
        AuthorityUtils.commaSeparatedStringToAuthorityList(role), launchRequest.getOauth_consumer_key(), launchRequest);
  }

}
