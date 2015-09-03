/**
 * 
 */
package od.providers;

import java.util.Map;

import od.providers.assignment.AssignmentsProvider;
import od.providers.course.CourseProvider;
import od.providers.events.EventProvider;
import od.providers.forum.ForumsProvider;
import od.providers.modeloutput.ModelOutputProvider;
import od.providers.outcomes.OutcomesProvider;
import od.providers.roster.RosterProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ggilbert
 *
 */
@Component
public class ProviderService {
  @Autowired private Map<String, AssignmentsProvider> assignmentProviders;
  @Autowired private Map<String, CourseProvider> courseProviders;
  @Autowired private Map<String, EventProvider> eventProviders;
  @Autowired private Map<String, ForumsProvider> forumsProviders;
  @Autowired private Map<String, ModelOutputProvider> modelOutputProviders;
  @Autowired private Map<String, OutcomesProvider> outcomesProviders;
  @Autowired private Map<String, RosterProvider> rosterProviders;
  
  public AssignmentsProvider getAssignmentsProvider(String key) {
    return assignmentProviders.get(key);
  }
  
  public CourseProvider getCourseProvider(String key) {
    return courseProviders.get(key);
  }
  
  public EventProvider getEventProvider(String key) {
    return eventProviders.get(key);
  }

  public ForumsProvider getForumsProvider(String key) {
    return forumsProviders.get(key);
  }

  public ModelOutputProvider getModelOutputProvider(String key) {
    return modelOutputProviders.get(key);
  }
  
  public OutcomesProvider getOutcomesProvider(String key) {
    return outcomesProviders.get(key);
  }
  
  public RosterProvider getRosterProvider(String key) {
    return rosterProviders.get(key);
  }
  

}
