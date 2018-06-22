package org.boot.uploader.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author luoliang
 * @date 2018/6/20
 */
public class FileUtils {

    public static String generatePath(String uploadFolder, String identifier, String originalFilename, Integer chunkNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(uploadFolder).append("/").append(identifier);
        //判断uploadFolder/identifier 路径是否存在，不存在则创建
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

    /**
     * 文件合并
     *
     * @param targetFile
     * @param folder
     */
    public static void merge(String targetFile, String folder) {
        try {
            Files.createFile(Paths.get(targetFile));
            Files.list(Paths.get(folder))
                    .filter(path -> path.getFileName().toString().contains("-"))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf("-");
                        int i2 = p2.lastIndexOf("-");
                        return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                    })
                    .forEach(path -> {
                        try {
                            //以追加的形式写入文件
                            Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            //合并后删除该块
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
