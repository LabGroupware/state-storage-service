package org.cresplanex.api.state.storageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.saga.local.storage.AlreadyExistFileObjectPathInBucketException;
import org.cresplanex.api.state.common.service.BaseService;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.exception.NotFoundFileObjectException;
import org.cresplanex.api.state.storageservice.exception.FileObjectNotFoundException;
import org.cresplanex.api.state.storageservice.repository.FileObjectRepository;
import org.cresplanex.api.state.storageservice.saga.model.fileobject.CreateFileObjectSaga;
import org.cresplanex.api.state.storageservice.saga.state.fileobject.CreateFileObjectSagaState;
import org.cresplanex.core.saga.orchestration.SagaInstanceFactory;
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
    public List<FileObjectEntity> get() {
        return fileObjectRepository.findAll();
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
        List<String> existFileObjectIds = fileObjectRepository.findAllByBucketIdIn(fileObjectIds)
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
}
