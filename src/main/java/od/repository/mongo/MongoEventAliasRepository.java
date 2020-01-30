/**
 * 
 */
package od.repository.mongo;

import od.framework.model.EventAlias;
import od.framework.model.Tenant;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author scody
 *
 */
public interface MongoEventAliasRepository extends MongoRepository<EventAlias, String> {
  List<EventAlias> findByTenantId(String tenantId);
}
