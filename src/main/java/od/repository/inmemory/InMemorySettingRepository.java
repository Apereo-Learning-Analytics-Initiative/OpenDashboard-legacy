package od.repository.inmemory;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import od.framework.model.Setting;
import od.repository.SettingRepositoryInterface;

@Profile("inmemory")
@Component
public class InMemorySettingRepository implements SettingRepositoryInterface{

    @Override
    public List<Setting> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Setting save(Setting setting) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Setting findOne(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub
        
    }

}
