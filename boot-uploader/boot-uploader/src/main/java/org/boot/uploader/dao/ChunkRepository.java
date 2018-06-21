package org.boot.uploader.dao;

import org.boot.uploader.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author luoliang
 * @date 2018/6/21
 */
public interface ChunkRepository extends JpaRepository<Chunk, Long>, JpaSpecificationExecutor<Chunk> {
}
