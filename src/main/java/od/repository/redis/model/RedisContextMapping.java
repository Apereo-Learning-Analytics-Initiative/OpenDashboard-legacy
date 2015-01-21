/**
 *
 */
package od.repository.redis.model;

import java.util.Date;

import od.model.OpenDashboardModel;


/**
 * @author ggilbert
 *
 */
public class RedisContextMapping extends OpenDashboardModel {
    private static final long serialVersionUID = 1L;

    private String key;
    private String context;
    private Date modified;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public Date getModified() {
        return modified;
    }
    public void setModified(Date modified) {
        this.modified = modified;
    }
}
