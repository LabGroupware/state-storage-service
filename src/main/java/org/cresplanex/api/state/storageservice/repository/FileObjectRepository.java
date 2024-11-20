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

    @Query("SELECT u FROM FileObjectEntity u WHERE u.fileObjectId IN :fileObject ORDER BY " +
            "CASE WHEN :sortType = 'CREATED_AT_ASC' THEN u.createdAt END ASC, " +
            "CASE WHEN :sortType = 'CREATED_AT_DESC' THEN u.createdAt END DESC, " +
            "CASE WHEN :sortType = 'NAME_ASC' THEN u.name END ASC, " +
            "CASE WHEN :sortType = 'NAME_DESC' THEN u.name END DESC")
    List<FileObjectEntity> findListByFileObjectIds(List<String> fileObjectIds, FileObjectSortType sortType);

    @Query("SELECT u FROM FileObjectEntity u ORDER BY " +
            "CASE WHEN :sortType = 'CREATED_AT_ASC' THEN u.createdAt END ASC, " +
            "CASE WHEN :sortType = 'CREATED_AT_DESC' THEN u.createdAt END DESC, " +
            "CASE WHEN :sortType = 'NAME_ASC' THEN u.name END ASC, " +
            "CASE WHEN :sortType = 'NAME_DESC' THEN u.name END DESC")
    List<FileObjectEntity> findList(Specification<FileObjectEntity> specification, FileObjectSortType sortType);

    @Query("SELECT u FROM FileObjectEntity u ORDER BY " +
            "CASE WHEN :sortType = 'CREATED_AT_ASC' THEN u.createdAt END ASC, " +
            "CASE WHEN :sortType = 'CREATED_AT_DESC' THEN u.createdAt END DESC, " +
            "CASE WHEN :sortType = 'NAME_ASC' THEN u.name END ASC, " +
            "CASE WHEN :sortType = 'NAME_DESC' THEN u.name END DESC")
    List<FileObjectEntity> findListWithOffsetPagination(Specification<FileObjectEntity> specification,
                                                            FileObjectSortType sortType, Pageable pageable);

    @Query("SELECT COUNT(u) FROM FileObjectEntity u")
    int countList(Specification<FileObjectEntity> specification);
}
