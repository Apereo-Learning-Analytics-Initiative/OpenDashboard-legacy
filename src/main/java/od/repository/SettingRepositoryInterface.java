package od.repository;

import java.util.List;

import od.framework.model.Setting;

public interface SettingRepositoryInterface {
    List<Setting> findAll();
    Setting save(final Setting setting);
    Setting findOne(final String id);
    void delete(final String id);
}
