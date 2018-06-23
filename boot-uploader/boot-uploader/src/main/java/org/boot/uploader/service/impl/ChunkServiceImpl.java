package org.boot.uploader.service.impl;

import org.boot.uploader.dao.ChunkRepository;
import org.boot.uploader.model.Chunk;
import org.boot.uploader.service.ChunkService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/6/21
 */
@Service
public class ChunkServiceImpl implements ChunkService {
    @Resource
    private ChunkRepository chunkRepository;

    @Override
    public void saveChunk(Chunk chunk) {
        chunkRepository.save(chunk);
    }

    @Override
    public boolean checkChunk(String identifier, Integer chunkNumber) {
        Specification<Chunk> specification = (Specification<Chunk>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("identifier"), identifier));
            predicates.add(criteriaBuilder.equal(root.get("chunkNumber"), chunkNumber));

            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };

        return chunkRepository.findOne(specification).orElse(null) == null;
    }

}
