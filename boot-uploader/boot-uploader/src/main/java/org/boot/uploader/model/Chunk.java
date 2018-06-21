package org.boot.uploader.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author luoliang
 * @date 2018/6/19
 */
@Data
@Entity
public class Chunk implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 当前文件块，从1开始
     */
    @Column(nullable = false)
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    @Column(nullable = false)
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    @Column(nullable = false)
    private Long currentChunkSize;
    /**
     * 总大小
     */
    @Column(nullable = false)
    private Long totalSize;
    /**
     * 文件标识
     */
    @Column(nullable = false)
    private String identifier;
    /**
     * 文件名
     */
    @Column(nullable = false)
    private String filename;
    /**
     * 相对路径
     */
    @Column(nullable = false)
    private String relativePath;
    /**
     * 总块数
     */
    @Column(nullable = false)
    private Integer totalChunks;
    /**
     * 文件类型
     */
    @Column
    private String type;
    @Transient
    private MultipartFile file;
}
