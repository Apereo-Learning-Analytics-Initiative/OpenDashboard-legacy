/**
 * 
 */
package od.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class OpenDashboardUserDetailsService implements UserDetailsService {

  final static Logger log = LoggerFactory.getLogger(OpenDashboardUserDetailsService.class);

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info(username);

    return new OpenDashboardUser("userid", "userid", 
        AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"), "key", null);
  }

}
