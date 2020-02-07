/**
 * 
 */
package od.repository.mongo;

import od.framework.model.PulseClassDetail;
import od.framework.model.PulseDetail;
import od.framework.model.Tenant;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author ggilbert
 *
 */
public interface PulseClassCacheRepository extends MongoRepository<PulseClassDetail, String> { 
	
	List<PulseClassDetail> findById(String classSourcedId);
  
  

}
