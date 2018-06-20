package org.boot.uploader.service.impl;

import org.boot.uploader.dao.FileInfoRepository;
import org.boot.uploader.model.FileInfo;
import org.boot.uploader.service.FileInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/6/20
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {
    @Resource
    private FileInfoRepository fileInfoRepository;

    @Override
    public FileInfo addFileInfo(FileInfo fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }
}
