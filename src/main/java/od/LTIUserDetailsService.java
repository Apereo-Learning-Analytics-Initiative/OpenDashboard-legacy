/**
 * 
 */
package od;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
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
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		String [] credentials = StringUtils.split(username, ":");
		return new User(username, credentials[1], AuthorityUtils.commaSeparatedStringToAuthorityList(credentials[2]));
	}

}
