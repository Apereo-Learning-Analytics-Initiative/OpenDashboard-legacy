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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class LTIAuthenticationProvider implements AuthenticationProvider {
  final static Logger log = LoggerFactory.getLogger(LTIAuthenticationProvider.class);

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    log.info(authentication.toString());
    // Nothing to do, just return it
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(LTIAuthenticationToken.class);
  }

}
