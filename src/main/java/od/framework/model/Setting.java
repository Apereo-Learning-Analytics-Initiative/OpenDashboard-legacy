package od.framework.model;

public class Setting extends OpenDashboardModel{
    private static final long serialVersionUID = -4239472027038567535L;
    
    private String key;
    private String value;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "Setting [key=" + key + ", value=" + value + "]";
    }
}
