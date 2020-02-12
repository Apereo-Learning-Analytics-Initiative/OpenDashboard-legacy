/*package od.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import od.framework.model.Tenant;
import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.ProviderService;
import od.providers.config.ProviderDataConfigurationException;
import od.providers.enrollment.EnrollmentProvider;
import od.repository.mongo.MongoTenantRepository;
import unicon.matthews.oneroster.Enrollment;

@Configuration
@Component
@Service
public class PulseCacheTask {

    @Autowired PulseCacheService pulseCacheService;
    @Autowired private ProviderService providerService;
    @Autowired private MongoTenantRepository mongoTenantRepository;
    
    @Value("${cacheProcess.runCaching}")
    private boolean shouldCache ;
    
    
    //scheduled every 24 hours
    //@Scheduled(cron = "${cacheProcess.cronExpression}")  
    @ConditionalOnProperty(value = "cacheProcess.runCaching", havingValue = "true")
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) 
    public void updatePulseCache() {  
    	if(!shouldCache) {
    		return;
    	}
    	
    
      List<Tenant> tenants = mongoTenantRepository.findAll();
      
      for(Tenant tenant: tenants) {
        ProviderData rosterProviderData;
        try {
          rosterProviderData = providerService.getConfiguredProviderDataByType(tenant, ProviderService.ROSTER);        
          EnrollmentProvider enrollmentProvider = providerService.getRosterProvider(tenant);
          
          List<String> teacherIds = enrollmentProvider.getUniqueUsersWithRole(rosterProviderData, "teacher");
          if (teacherIds == null) {
            continue;
          }          

          //since this is a multi-server environment
          //let's randomize the order in which we update. 
          //This will make it far less likely that we step on each other's toes
          int index_orig = 0; 
          while(teacherIds.size()>0)  
          {
            index_orig = index_orig+1;  
            int index = new Random().nextInt(teacherIds.size());
            
            
            String userId = teacherIds.get(index);
            
            Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForUser(rosterProviderData, userId, true);
            for(Enrollment enrollment: enrollments) {              
              Set<Enrollment> newSet = new HashSet<Enrollment>();
              newSet.add(enrollment);
              syncPulse(tenant.getId(),userId,enrollment.getKlass().getSourcedId(), newSet);
            }
            
            //remove the id
            teacherIds.remove(index);
          }
          
        } catch (ProviderDataConfigurationException e) {
          e.printStackTrace();
        } catch (ProviderException e) {
          e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
      }     
        
    }    
    
    //TODO: We could make this @Async
    List<CompletableFuture<String>> results = new ArrayList<>();
    public void syncPulse(String tenantId, String userId, String classSourcedId, Set<Enrollment> enrollments)  {      
      try {
        results.add(pulseCacheService.pulseCache(tenantId, userId, classSourcedId, enrollments));
      } catch (ProviderDataConfigurationException | ProviderException e) {
        e.printStackTrace();
      } catch (RuntimeException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }           
    }
}
*/