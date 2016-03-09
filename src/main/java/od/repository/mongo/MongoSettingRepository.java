package od.repository.mongo;

import od.framework.model.Setting;
import od.repository.SettingRepositoryInterface;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoSettingRepository extends SettingRepositoryInterface, MongoRepository<Setting, String> {
}
