package org.boot.uploader.service;

import org.boot.uploader.model.Chunk;

/**
 * @author luoliang
 * @date 2018/6/21
 */
public interface ChunkService {
    void saveChunk(Chunk chunk);

    Chunk getChunk(String identifier, Integer chunkNumber);
}
