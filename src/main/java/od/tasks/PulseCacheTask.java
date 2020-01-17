package od.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Component
@Service
public class PulseCacheTask {

    @Autowired PulseCacheService pulseCacheService;
    @Autowired private ProviderService providerService;
    @Autowired private MongoTenantRepository mongoTenantRepository;
    
    //scheduled every 24 hours
    @Scheduled(cron = "${cacheProcess.cronExpression}")  
    @ConditionalOnProperty("${cacheProcess.runCaching}")
    public void updatePulseCache() {  
      
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
          while(teacherIds.size()>0)  
          {
        	  
            int index = new Random().nextInt(teacherIds.size());
            System.out.println("updating for teacher: " + teacherIds.get(index));
            String userId = teacherIds.get(index);
            
            Set<Enrollment> enrollments = enrollmentProvider.getEnrollmentsForUser(rosterProviderData, userId, true);
            for(Enrollment enrollment: enrollments) {
              syncPulse(tenant.getId(),userId,enrollment.getKlass().getSourcedId());
            }
            
            //remove the id
            teacherIds.remove(index);
          }
          
        } catch (ProviderDataConfigurationException e) {
          e.printStackTrace();
        } catch (ProviderException e) {
          e.printStackTrace();
        } 
      }     
        
    }    
    
    //TODO: We could make this @Async
    public void syncPulse(String tenantId, String userId, String classSourcedId)  {
      List<CompletableFuture<String>> results = new ArrayList<>();
      try {
        results.add(pulseCacheService.pulseCache(tenantId, userId, classSourcedId));
      } catch (ProviderDataConfigurationException | ProviderException e) {
        e.printStackTrace();
      } catch (RuntimeException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }           
    }
}