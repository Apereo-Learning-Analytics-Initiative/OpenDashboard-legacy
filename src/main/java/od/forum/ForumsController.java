/**
 * 
 */
package od.forum;

import java.util.List;

import od.providers.ProviderOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggilbert
 *
 */
@RestController
public class ForumsController {

  private static final Logger log = LoggerFactory.getLogger(ForumsController.class);

  @Autowired
  private ForumsProvider forumsProvider;

  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/forums", method = RequestMethod.POST)
  public List<Forum> forums(@RequestBody ProviderOptions providerOptions) throws Exception {

    if (log.isDebugEnabled()) {
      log.debug(providerOptions.toString());
    }

    return forumsProvider.getForums(providerOptions);
  }
  
  @Secured("ROLE_INSTRUCTOR")
  @RequestMapping(value = "/api/forums/{id}/messages", method = RequestMethod.POST)
  public List<Message> messages(@RequestBody ProviderOptions providerOptions, @PathVariable("id") final String id) throws Exception {

    if (log.isDebugEnabled()) {
      log.debug(providerOptions.toString());
      log.debug("topic id: "+id);
    }

    return forumsProvider.getMessages(providerOptions, id);
  }

}
