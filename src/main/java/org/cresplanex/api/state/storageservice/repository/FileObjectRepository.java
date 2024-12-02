package org.cresplanex.api.state.storageservice.repository;

import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.enums.FileObjectSortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileObjectRepository extends JpaRepository<FileObjectEntity, String>, JpaSpecificationExecutor<FileObjectEntity> {

    List<FileObjectEntity> findByBucketId(String bucketId);

    List<FileObjectEntity> findAllByBucketIdIn(List<String> bucketIds);

    Optional<FileObjectEntity> findByBucketIdAndPath(String bucketId, String path);

    @Query("SELECT u FROM FileObjectEntity u")
    List<FileObjectEntity> findList(Specification<FileObjectEntity> specification, Pageable pageable);

    @Query("SELECT COUNT(u) FROM FileObjectEntity u")
    int countList(Specification<FileObjectEntity> specification);
}
