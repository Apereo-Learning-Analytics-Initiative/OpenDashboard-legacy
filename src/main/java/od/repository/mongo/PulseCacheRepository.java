/**
 * 
 */
package od.repository.mongo;

import od.framework.model.PulseDetail;
import od.framework.model.Tenant;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author ggilbert
 *
 */
public interface PulseCacheRepository extends MongoRepository<PulseDetail, String> {  
  List<PulseDetail> findByUserIdAndTenantIdAndUserRole(String userId, String tenantId, String string);
  void deleteByUserIdAndTenantIdAndUserRole(String userId, String tenantId, String string);
}
