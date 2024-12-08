package org.cresplanex.api.state.storageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.saga.local.storage.AlreadyExistFileObjectPathInBucketException;
import org.cresplanex.api.state.common.service.BaseService;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.exception.FileObjectNotFoundException;
import org.cresplanex.api.state.storageservice.exception.NotFoundFileObjectException;
import org.cresplanex.api.state.storageservice.repository.FileObjectRepository;
import org.cresplanex.api.state.storageservice.saga.model.fileobject.CreateFileObjectSaga;
import org.cresplanex.api.state.storageservice.saga.state.fileobject.CreateFileObjectSagaState;
import org.cresplanex.core.saga.orchestration.SagaInstanceFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileObjectLocalValidateService extends BaseService {

    private final FileObjectRepository fileObjectRepository;

    public void validateCreatedFileObject(String bucketId, String name, String path)
            throws AlreadyExistFileObjectPathInBucketException {
//        fileObjectRepository.findByBucketIdAndPath(bucketId, path)
//                .ifPresent(organization -> {
//                    throw new AlreadyExistFileObjectPathInBucketException(bucketId, List.of(path));
//                }); // TODO: uncomment this code
    }
}
