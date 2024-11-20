package org.cresplanex.api.state.storageservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.filter.fileobject.BucketFilter;
import org.springframework.data.jpa.domain.Specification;

public class FileObjectSpecifications {

    public static Specification<FileObjectEntity> withBucketFilter(BucketFilter bucketFilter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (bucketFilter != null && bucketFilter.isValid()) {
                if (bucketFilter.getBucketIds() != null && !bucketFilter.getBucketIds().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("bucketId").in(bucketFilter.getBucketIds()));
                }
            }
            return predicate;
        };
    }
}
