package org.boot.uploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.boot.uploader.model.FileInfo;
import org.boot.uploader.model.UploadParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author luoliang
 * @date 2018/6/19
 */
@RestController
@RequestMapping("/uploader")
@Slf4j
public class UploadController {
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;

    @PostMapping("/chunk")
    public String chunk(UploadParam uploadParam) {
        log.debug(uploadParam.toString());
        MultipartFile file = uploadParam.getFile();
        log.debug("file originName:{}, chunkNumber:{}", file.getOriginalFilename(), uploadParam.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename() + "-" + uploadParam.getChunkNumber());
            //如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(UPLOAD_FOLDER));
            }
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件写入成功...");

            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }

    @PostMapping("/mergeFile")
    public String mergeFile(FileInfo fileInfo) {


        return "";
    }

    void merge() throws IOException {
        String destName = "files/" + "test.mp4";
        Path destFile = Paths.get(destName);
        Files.createFile(destFile);
        Files.list(Paths.get("files/"))
                .filter(path -> path.getFileName().toString().contains("-"))
                .sorted((o1, o2) -> {
                    String p1 = o1.getFileName().toString();
                    String p2 = o2.getFileName().toString();
                    System.out.println(p1 + "," + p2);
                    int i1 = p1.lastIndexOf("-");
                    int i2 = p2.lastIndexOf("-");
                    return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                })
                .forEach(path -> {
                    try {
                        System.out.println(path.getFileName());
                        Files.write(destFile, Files.readAllBytes(path), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
