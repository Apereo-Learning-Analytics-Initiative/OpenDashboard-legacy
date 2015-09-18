/**
 * 
 */
package od.providers.modeloutput;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.ModelOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author ggilbert
 *
 */
public interface ModelOutputProvider extends Provider {
  
  Page<ModelOutput> getModelOutputForCourse(ProviderOptions options, String course, Pageable page) throws ProviderException;
  Page<ModelOutput> getModelOutputForStudent(ProviderOptions options, String student, Pageable page) throws ProviderException;
}
