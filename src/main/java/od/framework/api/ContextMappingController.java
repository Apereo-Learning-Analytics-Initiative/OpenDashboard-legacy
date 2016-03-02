/**
 *
 */
package od.framework.api;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import od.framework.model.Card;
import od.framework.model.ContextMapping;
import od.framework.model.Dashboard;
import od.repository.ContextMappingRepositoryInterface;
import od.repository.PreconfiguredDashboardRepositoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ggilbert
 *
 */
@RestController
public class ContextMappingController {
  private static final Logger log = LoggerFactory.getLogger(ContextMappingController.class);
  @Autowired private ContextMappingRepositoryInterface contextMappingRepository;
  @Autowired private PreconfiguredDashboardRepositoryInterface preconfiguredDashboardRepository;
		
	@Secured("ROLE_INSTRUCTOR")
	@RequestMapping(value = "/api/consumer/{consumerKey}/context", method = RequestMethod.POST, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public ContextMapping create(@RequestBody ContextMapping contextMapping) {
		
		try {
		  List<Dashboard> dashboards = preconfiguredDashboardRepository.findAll();
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
	@RequestMapping(value = "/api/consumer/{consumerKey}/context/{context}", method = RequestMethod.PUT, 
			produces = "application/json;charset=utf-8", consumes = "application/json")
	public ContextMapping update(@RequestBody ContextMapping contextMapping) {
		
		contextMapping.setModified(new Date());
		return contextMappingRepository.save(contextMapping);
	}

	@Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
	@RequestMapping(value = "/api/consumer/{consumerKey}/context/{context}", method = RequestMethod.GET, 
			produces = "application/json;charset=utf-8")
	public ContextMapping get(@PathVariable("consumerKey") final String consumerKey,
								@PathVariable("context") final String context) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("get ContextMapping for %s and %s", consumerKey,context));
		}
		
		return contextMappingRepository.findByKeyAndContext(consumerKey, context);
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
