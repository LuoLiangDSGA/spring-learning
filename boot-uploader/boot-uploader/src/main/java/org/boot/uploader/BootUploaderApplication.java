package org.boot.uploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

/**
 * @author luoliang
 */
@SpringBootApplication
public class BootUploaderApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(BootUploaderApplication.class, args);
    }
}
