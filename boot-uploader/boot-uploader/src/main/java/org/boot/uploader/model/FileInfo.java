package org.boot.uploader.model;

import lombok.Data;

/**
 * @author luoliang
 * @date 2018/6/20
 */
@Data
public class FileInfo {
    private String id;

    private String filename;

    private String identifier;

    private Long fileSize;
}
