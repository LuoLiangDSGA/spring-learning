package my.boot.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author luoliang
 * @date 2019/2/26
 */
public class MyDataTemplate {
    private Logger logger = LoggerFactory.getLogger(MyDataTemplate.class);

    private String url;

    private String type;

    public MyDataTemplate(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public String getData() {
        logger.info("=========> get data from: ({}), type=({})", url, type);

        return String.format("%s-%s(time：%s)", url, type, LocalDateTime.now());
    }
}
