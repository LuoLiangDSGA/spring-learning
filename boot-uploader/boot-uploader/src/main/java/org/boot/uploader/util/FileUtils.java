package org.boot.uploader.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author luoliang
 * @date 2018/6/20
 */
public class FileUtils {

    public static String generatePath(String uploadFolder, String identifier, String originalFilename, Integer chunkNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(uploadFolder).append("/").append(identifier);
        //判断uploadFolder/uuid 路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(sb.toString()))) {
            try {
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.append("/")
                .append(originalFilename)
                .append("-")
                .append(chunkNumber).toString();
    }
}
