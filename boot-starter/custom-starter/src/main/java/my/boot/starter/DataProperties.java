package my.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author luoliang
 * @date 2019/2/26
 */
@ConfigurationProperties(prefix = "data")
public class DataProperties {
    public static final String DEFAULT_URI = "localhost:3306";
    public static final String DEFAULT_TYPE = "mysql";
    public static final boolean DEFAULT_ENABLED = false;
    private Boolean enabled;
    private String uri;
    private String type;

    public boolean getEnabled() {
        return this.enabled != null ? this.enabled : DEFAULT_ENABLED;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUri() {
        return this.uri != null ? this.uri : DEFAULT_URI;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return this.type != null ? this.type : DEFAULT_TYPE;
    }

    public void setType(String type) {
        this.type = type;
    }
}
