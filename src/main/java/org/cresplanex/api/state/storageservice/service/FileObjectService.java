package org.cresplanex.api.state.storageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.entity.ListEntityWithCount;
import org.cresplanex.api.state.common.enums.PaginationType;
import org.cresplanex.api.state.common.service.BaseService;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.enums.FileObjectSortType;
import org.cresplanex.api.state.storageservice.exception.NotFoundFileObjectException;
import org.cresplanex.api.state.storageservice.exception.FileObjectNotFoundException;
import org.cresplanex.api.state.storageservice.filter.fileobject.BucketFilter;
import org.cresplanex.api.state.storageservice.repository.FileObjectRepository;
import org.cresplanex.api.state.storageservice.saga.model.fileobject.CreateFileObjectSaga;
import org.cresplanex.api.state.storageservice.saga.state.fileobject.CreateFileObjectSagaState;
import org.cresplanex.api.state.storageservice.specification.FileObjectSpecifications;
import org.cresplanex.core.saga.orchestration.SagaInstanceFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileObjectService extends BaseService {

    private final FileObjectRepository fileObjectRepository;
    private final SagaInstanceFactory sagaInstanceFactory;

    private final CreateFileObjectSaga createFileObjectSaga;

    @Transactional(readOnly = true)
    public FileObjectEntity findById(String fileObjectId) {
        return internalFindById(fileObjectId);
    }

    private FileObjectEntity internalFindById(String fileObjectId) {
        return fileObjectRepository.findById(fileObjectId).orElseThrow(() -> new FileObjectNotFoundException(
                FileObjectNotFoundException.FindType.BY_ID,
                fileObjectId
        ));
    }

    @Transactional(readOnly = true)
    public ListEntityWithCount<FileObjectEntity> get(
            PaginationType paginationType,
            int limit,
            int offset,
            String cursor,
            FileObjectSortType sortType,
            boolean withCount,
            BucketFilter bucketFilter
    ) {
        Specification<FileObjectEntity> spec = Specification.where(
                FileObjectSpecifications.withBucketFilter(bucketFilter));

        Sort sort = createSort(sortType);

        Pageable pageable = switch (paginationType) {
            case OFFSET -> PageRequest.of(offset / limit, limit, sort);
            case CURSOR -> PageRequest.of(0, limit, sort); // TODO: Implement cursor pagination
            default -> Pageable.unpaged(sort);
        };

        Page<FileObjectEntity> data = fileObjectRepository.findAll(spec, pageable);

        int count = 0;
        if (withCount){
            count = (int) data.getTotalElements();
        }
        return new ListEntityWithCount<>(
                data.getContent(),
                count
        );
    }

    @Transactional(readOnly = true)
    public List<FileObjectEntity> getByFileObjectIds(
            List<String> fileObjectIds,
            FileObjectSortType sortType
    ) {
        Specification<FileObjectEntity> spec = Specification.where(
                FileObjectSpecifications.whereFileObjectIds(fileObjectIds)
        );
        return fileObjectRepository.findAll(spec, createSort(sortType));
    }

    public String beginCreate(String operatorId, FileObjectEntity object) {
        CreateFileObjectSagaState.InitialData initialData = CreateFileObjectSagaState.InitialData.builder()
                .bucketId(object.getBucketId())
                .name(object.getName())
                .path(object.getPath())
                .build();
        CreateFileObjectSagaState state = new CreateFileObjectSagaState();
        state.setInitialData(initialData);
        state.setOperatorId(operatorId);

        String jobId = getJobId();
        state.setJobId(jobId);

        sagaInstanceFactory.create(createFileObjectSaga, state);

        return jobId;
    }

    public FileObjectEntity create(String operatorId, FileObjectEntity object) {
        return fileObjectRepository.save(object);
    }

    public void undoCreate(String fileObjectId) {
        fileObjectRepository.deleteById(fileObjectId);
    }

    public void validateFileObjects(List<String> fileObjectIds) {
        List<String> existFileObjectIds = fileObjectRepository.findAllByFileObjectIdIn(fileObjectIds)
                .stream()
                .map(FileObjectEntity::getFileObjectId)
                .toList();
        if (existFileObjectIds.size() != fileObjectIds.size()) {
            List<String> notExistFileObjectIds = fileObjectIds.stream()
                    .filter(objectId -> !existFileObjectIds.contains(objectId))
                    .toList();
            throw new NotFoundFileObjectException(notExistFileObjectIds);
        }
    }

    private Sort createSort(FileObjectSortType sortType) {
        return switch (sortType) {
            case CREATED_AT_ASC -> Sort.by(Sort.Order.asc("createdAt"));
            case CREATED_AT_DESC -> Sort.by(Sort.Order.desc("createdAt"));
            case NAME_ASC -> Sort.by(Sort.Order.asc("name"), Sort.Order.desc("createdAt"));
            case NAME_DESC -> Sort.by(Sort.Order.desc("name"), Sort.Order.desc("createdAt"));
        };
    }
}
