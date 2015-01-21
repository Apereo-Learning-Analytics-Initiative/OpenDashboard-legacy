/**
 *
 */
package od.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lti.LaunchRequest;

/**
 * @author ggilbert
 *
 */
public class Session extends OpenDashboardModel {
    private Map<String, LaunchRequest> data = new LinkedHashMap<String, LaunchRequest>();;
    private long timestamp;

    public Map<String, LaunchRequest> getData() {
        return data;
    }
    public void setData(Map<String, LaunchRequest> data) {
        this.data = data;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
