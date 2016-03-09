package od.framework.api;

import java.util.ArrayList;
import java.util.List;

import od.framework.model.Setting;
import od.repository.SettingRepositoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    @Autowired private SettingRepositoryInterface settingRepositoryInterface;
    
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/setting", method = RequestMethod.POST, 
        produces = "application/json;charset=utf-8", consumes = "application/json")
    public Setting create(@RequestBody Setting setting) {
      return settingRepositoryInterface.save(setting);
    }
    
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/setting", method = RequestMethod.PUT, 
        produces = "application/json;charset=utf-8", consumes = "application/json")
    public List<Setting> update(@RequestBody List<Setting> settings) {
        List<Setting> list = new ArrayList<>();
        Setting s;
        for (Setting setting : settings){
           s = settingRepositoryInterface.save(setting);
           list.add(s);
        }
        return list;
    }
    
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/setting", method = RequestMethod.GET, 
        produces = "application/json;charset=utf-8")
    public List<Setting> getAll() {
      return settingRepositoryInterface.findAll();
    }
    
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/setting/{id}", method = RequestMethod.DELETE, 
        produces = "application/json;charset=utf-8")
    public void delete(@PathVariable("id") final String id) {
        settingRepositoryInterface.delete(id);
    }
}
