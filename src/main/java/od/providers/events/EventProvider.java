/**
 * 
 */
package od.providers.events;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author ggilbert
 *
 */
public interface EventProvider extends Provider {
  
  Page<Event> getEventsForUser(ProviderOptions options, Pageable pageable) throws ProviderException;
  Page<Event> getEventsForCourse(ProviderOptions options, Pageable pageable) throws ProviderException;
  Page<Event> getEventsForCourseAndUser(ProviderOptions options, Pageable pageable) throws ProviderException;
}
