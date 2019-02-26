package my.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author luoliang
 * @date 2019/2/26
 */
@ConfigurationProperties(prefix = "data")
public class DataProperties {
    private String url;

    private String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
