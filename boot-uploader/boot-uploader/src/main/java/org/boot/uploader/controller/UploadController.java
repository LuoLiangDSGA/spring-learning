package org.boot.uploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.boot.uploader.model.UploadParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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

    @RequestMapping("chunk")
    public String chunk(UploadParam uploadParam) {
        log.debug(uploadParam.toString());
        MultipartFile file = uploadParam.getFile();
        if (Objects.isNull(file) || file.isEmpty()) {
            log.error("文件为空");
            return "文件为空，请重新上传";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
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
}
