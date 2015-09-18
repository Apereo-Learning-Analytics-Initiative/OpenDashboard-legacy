/**
 * 
 */
package od.providers.course;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.Course;
import org.apereo.lai.impl.CourseImpl;

/**
 * @author ggilbert
 *
 */
public interface CourseProvider extends Provider {
  Course getContext(ProviderOptions options) throws ProviderException;
  List<CourseImpl> getContexts(ProviderOptions options) throws ProviderException;
}
