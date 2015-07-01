/**
 * 
 */
package od.course;

import java.util.List;
import java.util.Map;

import od.providers.ProviderException;
import od.providers.ProviderOptions;

/**
 * @author ggilbert
 *
 */
public interface CourseProvider {

  Course getContext(ProviderOptions options) throws ProviderException;
  List<Course> getContexts(ProviderOptions options) throws ProviderException;
}
