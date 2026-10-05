package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.entity.ProcessEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProcessCriteriaBuilder {

    private final EntityManager entityManager;

    @Transactional
    public void clearImages(List<Integer> processIds) {
        if (processIds.isEmpty()) {
            return;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaUpdate<ProcessEntity> update = cb.createCriteriaUpdate(ProcessEntity.class);

        Root<ProcessEntity> root = update.from(ProcessEntity.class);
        update.set(root.get("imageData"), (byte[]) null);
        update.where(
                root.get("id").in(processIds)
        );
        entityManager.createQuery(update).executeUpdate();
    }

}
