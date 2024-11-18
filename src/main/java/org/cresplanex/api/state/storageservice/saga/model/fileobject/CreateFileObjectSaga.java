package org.cresplanex.api.state.storageservice.saga.model.fileobject;

import org.cresplanex.api.state.common.constants.StorageServiceApplicationCode;
import org.cresplanex.api.state.common.event.model.storage.FileObjectCreated;
import org.cresplanex.api.state.common.event.model.storage.FileObjectDomainEvent;
import org.cresplanex.api.state.common.event.publisher.AggregateDomainEventPublisher;
import org.cresplanex.api.state.common.saga.SagaCommandChannel;
import org.cresplanex.api.state.common.saga.data.storage.CreateFileObjectResultData;
import org.cresplanex.api.state.common.saga.local.storage.AlreadyExistFileObjectPathInBucketException;
import org.cresplanex.api.state.common.saga.model.SagaModel;
import org.cresplanex.api.state.common.saga.reply.storage.CreateFileObjectReply;
import org.cresplanex.api.state.common.saga.type.StorageSagaType;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.event.publisher.FileObjectDomainEventPublisher;
import org.cresplanex.api.state.storageservice.saga.proxy.StorageServiceProxy;
import org.cresplanex.api.state.storageservice.saga.state.fileobject.CreateFileObjectSagaState;
import org.cresplanex.api.state.storageservice.service.FileObjectService;
import org.cresplanex.core.saga.orchestration.SagaDefinition;
import org.springframework.stereotype.Component;

@Component
public class CreateFileObjectSaga extends SagaModel<
        FileObjectEntity,
        FileObjectDomainEvent,
        CreateFileObjectSaga.Action,
        CreateFileObjectSagaState> {

    private final SagaDefinition<CreateFileObjectSagaState> sagaDefinition;
    private final FileObjectDomainEventPublisher domainEventPublisher;
    private final FileObjectService fileObjectLocalService;

    public CreateFileObjectSaga(
            FileObjectService fileObjectLocalService,
            StorageServiceProxy storageService,
            FileObjectDomainEventPublisher domainEventPublisher
    ) {
        this.sagaDefinition = step()
                .invokeLocal(this::validateFileObject)
                .onException(AlreadyExistFileObjectPathInBucketException.class, this::failureLocalExceptionPublish)
                .step()
                .invokeParticipant(
                        storageService.createFileObject,
                        CreateFileObjectSagaState::makeCreateFileObjectCommand
                )
                .onReply(
                        CreateFileObjectReply.Success.class,
                        CreateFileObjectReply.Success.TYPE,
                        this::handleCreateFileObjectReply
                )
                .onReply(
                        CreateFileObjectReply.Failure.class,
                        CreateFileObjectReply.Failure.TYPE,
                        this::handleFailureReply
                )
                .withCompensation(
                        storageService.undoCreateFileObject,
                        CreateFileObjectSagaState::makeUndoCreateFileObjectCommand
                )
                .build();
        this.fileObjectLocalService = fileObjectLocalService;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    protected AggregateDomainEventPublisher<FileObjectEntity, FileObjectDomainEvent>
    getDomainEventPublisher() {
        return domainEventPublisher;
    }

    @Override
    protected Action[] getActions() {
        return Action.values();
    }

    @Override
    protected String getBeginEventType() {
        return FileObjectCreated.BeginJobDomainEvent.TYPE;
    }

    @Override
    protected String getProcessedEventType() {
        return FileObjectCreated.ProcessedJobDomainEvent.TYPE;
    }

    @Override
    protected String getFailedEventType() {
        return FileObjectCreated.FailedJobDomainEvent.TYPE;
    }

    @Override
    protected String getSuccessfullyEventType() {
        return FileObjectCreated.SuccessJobDomainEvent.TYPE;
    }


    private void validateFileObject(CreateFileObjectSagaState state)
            throws AlreadyExistFileObjectPathInBucketException {
        this.fileObjectLocalService.validateCreatedFileObject(
                state.getInitialData().getBucketId(),
                state.getInitialData().getName(),
                state.getInitialData().getPath()
        );

        this.localProcessedEventPublish(
                state, StorageServiceApplicationCode.SUCCESS, "File object validated"
        );
    }

    private void handleCreateFileObjectReply(
            CreateFileObjectSagaState state, CreateFileObjectReply.Success reply) {
        CreateFileObjectReply.Success.Data data = reply.getData();
        state.setFileObjectDto(data.getFileObject());
        this.processedEventPublish(state, reply);
    }

    @Override
    public void onSagaCompletedSuccessfully(String sagaId, CreateFileObjectSagaState data) {
        CreateFileObjectResultData resultData = new CreateFileObjectResultData(
                data.getFileObjectDto()
        );
        successfullyEventPublish(data, resultData);
    }

    public enum Action {
        VALIDATE_FILE_OBJECT,
        CREATE_FILE_OBJECT
    }

    @Override
    public SagaDefinition<CreateFileObjectSagaState> getSagaDefinition() {
        return sagaDefinition;
    }

    @Override
    public String getSagaType() {
        return StorageSagaType.CREATE_FILE_OBJECT;
    }

    @Override
    public String getSagaCommandSelfChannel() {
        return SagaCommandChannel.STORAGE;
    }
}
