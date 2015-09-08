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
