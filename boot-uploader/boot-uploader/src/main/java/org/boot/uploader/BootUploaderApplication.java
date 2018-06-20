package org.boot.uploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * @author luoliang
 */
@SpringBootApplication
public class BootUploaderApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(BootUploaderApplication.class, args);
    }
}
