/**
 *
 */
package od.framework.api;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import od.framework.model.Card;
import od.framework.model.ContextMapping;
import od.framework.model.Dashboard;
import od.framework.model.Tenant;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.course.CourseProvider;
import od.repository.mongo.ContextMappingRepository;
import od.repository.mongo.MongoTenantRepository;

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
public class ContextMappingController {
  private static final Logger log = LoggerFactory.getLogger(ContextMappingController.class);
  @Autowired private ContextMappingRepository contextMappingRepository;
  @Autowired private MongoTenantRepository mongoTenantRepository;
  @Autowired private ProviderService providerService;
			
	 @Secured("ROLE_INSTRUCTOR")
	  @RequestMapping(value = "/api/consumer/context", method = RequestMethod.POST, 
	      produces = "application/json;charset=utf-8", consumes = "application/json")
	  public ContextMapping createWithTenantAndCourse(@RequestBody Map<String, String> params) {
	    
	   ContextMapping contextMapping = new ContextMapping();
	    try {
	      
	      Tenant tenant = mongoTenantRepository.findOne(params.get("tenantId"));
	      contextMapping.setTenantId(tenant.getId());
	      contextMapping.setContext(params.get("courseId"));
	      
	      Set<Dashboard> dashboards = tenant.getDashboards();
	      if (dashboards != null && !dashboards.isEmpty()) {
	        Set<Dashboard> dashboardSet = new HashSet<>();
	        for (Dashboard db : dashboards) {
	          db.setId(UUID.randomUUID().toString());
	          List<Card> cards = db.getCards();
	          if (cards != null && !cards.isEmpty()) {
	            for (Card c : cards) {
	              c.setId(UUID.randomUUID().toString());
	            }
	          }
	          dashboardSet.add(db);
	        }
	        contextMapping.setDashboards(dashboardSet);
	      }
	      
	    }
	    catch (Exception e) {
	      log.error("Unable to load preconfigured dashboards");
	      log.error(e.getMessage(),e);
	    }
	    
	    contextMapping.setModified(new Date());
	    return contextMappingRepository.save(contextMapping);
	  }

	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/consumer/context", method = RequestMethod.PUT, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public ContextMapping update(@RequestBody ContextMapping contextMapping) {
		
		contextMapping.setModified(new Date());
		return contextMappingRepository.save(contextMapping);
	}

	@Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
	@RequestMapping(value = "/api/consumer/{consumerKey}/context/{context}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public ContextMapping get(@PathVariable("consumerKey") final String consumerKey,
								@PathVariable("context") final String context) throws ProviderException, ProviderDataConfigurationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("get ContextMapping for %s and %s", consumerKey,context));
		}
		Tenant tenant = mongoTenantRepository.findByConsumersOauthConsumerKey(consumerKey);
    CourseProvider courseProvider = providerService.getCourseProvider(tenant);
    String courseId = courseProvider.getClassSourcedIdWithExternalId(tenant, context);
    ContextMapping cm = contextMappingRepository.findByTenantIdAndContext(tenant.getId(), courseId);
    
    return cm;
	}
	
  @Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
  @RequestMapping(value = "/api/tenant/{tenantId}/course/{courseId}", method = RequestMethod.GET, 
      produces = "application/json;charset=utf-8")
  public ContextMapping getWithTenantAndCourse(@PathVariable("tenantId") final String tenantId,
                @PathVariable("courseId") final String courseId) throws ProviderDataConfigurationException, ProviderException {
    
    if (log.isDebugEnabled()) {
      log.debug(String.format("get ContextMapping for %s and %s", tenantId,courseId));
    }
        
    return contextMappingRepository.findByTenantIdAndContext(tenantId, courseId);
  }
	
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/cm/{id}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public ContextMapping getById(@PathVariable("id") final String contextMappingId) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("get ContextMapping for %s", contextMappingId));
		}
		
		return contextMappingRepository.findOne(contextMappingId);
	}
}
