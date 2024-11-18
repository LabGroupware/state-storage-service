package org.cresplanex.api.state.storageservice.repository;

import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileObjectRepository extends JpaRepository<FileObjectEntity, String> {

    List<FileObjectEntity> findByBucketId(String bucketId);

    List<FileObjectEntity> findAllByBucketIdIn(List<String> bucketIds);

    Optional<FileObjectEntity> findByBucketIdAndPath(String bucketId, String path);
}
