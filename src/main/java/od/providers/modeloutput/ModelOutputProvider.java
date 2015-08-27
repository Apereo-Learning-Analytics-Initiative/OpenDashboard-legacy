/**
 * 
 */
package od.providers.modeloutput;

import java.util.List;

import od.providers.Provider;
import od.providers.ProviderException;
import od.providers.ProviderOptions;

import org.apereo.lai.ModelOutput;
import org.springframework.data.domain.Pageable;

/**
 * @author ggilbert
 *
 */
public interface ModelOutputProvider extends Provider {
  
  static final String DEFAULT = "modeloutput_lap";
  
  List<ModelOutput> getModelOutputForCourse(ProviderOptions options, String course, Pageable page) throws ProviderException;
  List<ModelOutput> getModelOutputForStudent(ProviderOptions options, String student, Pageable page) throws ProviderException;
}
