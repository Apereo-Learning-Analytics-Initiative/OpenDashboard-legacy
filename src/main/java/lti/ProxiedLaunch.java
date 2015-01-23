package lti;

import java.io.Serializable;
import java.util.Map;

public class ProxiedLaunch implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, String> params;
    private String launchUrl;

    public Map<String, String> getParams() {
        return params;
    }

    public String getLaunchUrl() {
        return launchUrl;
    }

    public ProxiedLaunch(Map<String, String> params, String launchUrl) {
        super();
        this.params = params;
        this.launchUrl = launchUrl;
    }
}
