package od.repository;

import java.util.List;

import od.framework.model.Setting;

public interface SettingRepositoryInterface {

  public static final String OAUTH_CONSUMER_KEY = "od.oauth_consumer_key";
  public static final String OAUTH_CONSUMER_SECRET = "od.oauth_consumer_secret";
  
  List<Setting> findAll();
  Setting save(final Setting setting);
  Setting findOne(final String id);
  void delete(final String id);
}
