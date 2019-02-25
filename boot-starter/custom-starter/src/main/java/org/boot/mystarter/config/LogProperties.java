package org.boot.mystarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author luoliang
 * @date 2019/2/22
 */
@ConfigurationProperties("mylog")
public class LogProperties {
    private String exclude;

    private String[] excludeArr;

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public String[] getExcludeArr() {
        return excludeArr;
    }

    @PostConstruct
    public void init() {
        this.excludeArr = StringUtils.split(exclude, ",");
    }
}
