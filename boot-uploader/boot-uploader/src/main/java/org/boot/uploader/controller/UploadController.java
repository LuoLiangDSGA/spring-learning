package org.boot.uploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.boot.uploader.model.Chunk;
import org.boot.uploader.model.FileInfo;
import org.boot.uploader.service.ChunkService;
import org.boot.uploader.service.FileInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.boot.uploader.util.FileUtils.generatePath;
import static org.boot.uploader.util.FileUtils.merge;

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
    @Resource
    private ChunkService chunkService;

    @PostMapping("/chunk")
    public String uploadChunk(Chunk chunk) {
        MultipartFile file = chunk.getFile();
        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(uploadFolder, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
            chunkService.saveChunk(chunk);

            return "文件上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "后端异常...";
        }
    }

    @GetMapping("/chunk")
    public Object checkChunk(Chunk chunk, HttpServletResponse response) {
        if (Objects.isNull(chunkService.checkChunk(chunk.getIdentifier(), chunk.getChunkNumber()))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        return chunk;
    }

    @PostMapping("/mergeFile")
    public String mergeFile(FileInfo fileInfo) {
        String path = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + fileInfo.getFilename();
        String folder = uploadFolder + "/" + fileInfo.getIdentifier();
        merge(path, folder);
        fileInfo.setLocation(path);
        fileInfoService.addFileInfo(fileInfo);

        return "合并成功";
    }
}
