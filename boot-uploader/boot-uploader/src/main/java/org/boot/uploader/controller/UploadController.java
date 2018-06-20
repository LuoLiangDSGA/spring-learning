package org.boot.uploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.boot.uploader.model.FileInfo;
import org.boot.uploader.model.UploadParam;
import org.boot.uploader.service.FileInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.boot.uploader.util.FileUtils.generatePath;

/**
 * @author luoliang
 * @date 2018/6/19
 */
@RestController
@RequestMapping("/uploader")
@Slf4j
public class UploadController {
    @Value("${prop.upload-folder}")
    private String uploadFolder;
    @Resource
    private FileInfoService fileInfoService;

    @PostMapping("/chunk")
    public String chunk(UploadParam uploadParam) {
        log.debug(uploadParam.toString());
        MultipartFile file = uploadParam.getFile();
        log.debug("file originName:{}, chunkNumber:{}", file.getOriginalFilename(), uploadParam.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(uploadFolder, uploadParam.getIdentifier(), uploadParam.getFilename(),
                    uploadParam.getChunkNumber()));

            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件{}写入成功,uuid:{}", uploadParam.getFilename(), uploadParam.getIdentifier());

            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }

    @PostMapping("/mergeFile")
    public String mergeFile(UploadParam uploadParam) {
        String path = uploadFolder + "/" + uploadParam.getIdentifier() + "/" + uploadParam.getFilename();
        String folder = uploadFolder + "/" + uploadParam.getIdentifier();
        merge(path, folder);
        FileInfo fileInfo = new FileInfo();
        BeanUtils.copyProperties(uploadParam, fileInfo);
        fileInfo.setLocation(path);
        fileInfoService.addFileInfo(fileInfo);

        return "合并成功";
    }

    void merge(String targetFile, String folder) {
        Path targetPath = Paths.get(targetFile);
        try {
            Files.createFile(targetPath);
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
                            Files.write(targetPath, Files.readAllBytes(path), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
