package org.cresplanex.api.state.storageservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.filter.fileobject.BucketFilter;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FileObjectSpecifications {

    public static Specification<FileObjectEntity> whereFileObjectIds(Iterable<String> fileObjectIds) {
        List<String> fileObjectIdList = new ArrayList<>();
        fileObjectIds.forEach(fileObjectId -> {
            fileObjectIdList.add(new StringJavaType().wrap(fileObjectId, null));
        });

        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, root.get("fileObjectId").in(fileObjectIdList));
            return predicate;
        };
    }

    public static Specification<FileObjectEntity> withBucketFilter(BucketFilter bucketFilter) {
        List<String> bucketList = new ArrayList<>();
        if (bucketFilter != null && bucketFilter.isValid()) {
            bucketFilter.getBucketIds().forEach(bucket -> {
                bucketList.add(new StringJavaType().wrap(bucket, null));
            });
        }

        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (bucketFilter != null && bucketFilter.isValid()) {
                predicate = criteriaBuilder.and(predicate, root.get("bucketId").in(bucketList));
            }
            return predicate;
        };
    }
}
