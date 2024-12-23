package org.cresplanex.api.state.storageservice.exception;

import build.buf.gen.storage.v1.*;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {

     @GrpcExceptionHandler(StorageNotFoundException.class)
     public Status handleStorageNotFoundException(StorageNotFoundException e) {
        StorageServiceStorageNotFoundError.Builder descriptionBuilder =
                StorageServiceStorageNotFoundError.newBuilder()
                .setMeta(buildErrorMeta(e));

        switch (e.getFindType()) {
            case BY_ID:
                descriptionBuilder
                        .setFindFieldType(StorageUniqueFieldType.STORAGE_UNIQUE_FIELD_TYPE_STORAGE_ID)
                        .setStorageId(e.getAggregateId());
                break;
        }

         return Status.NOT_FOUND
                 .withDescription(descriptionBuilder.build().toString())
                 .withCause(e);
     }

    @GrpcExceptionHandler(FileObjectNotFoundException.class)
    public Status handleFileObjectNotFoundException(FileObjectNotFoundException e) {
        StorageServiceFileObjectNotFoundError.Builder descriptionBuilder =
                StorageServiceFileObjectNotFoundError.newBuilder()
                        .setMeta(buildErrorMeta(e));

        switch (e.getFindType()) {
            case BY_ID:
                descriptionBuilder
                        .setFindFieldType(FileObjectUniqueFieldType.FILE_OBJECT_UNIQUE_FIELD_TYPE_FILE_OBJECT_ID)
                        .setFileObjectId(e.getAggregateId());
                break;
        }

        return Status.NOT_FOUND
                .withDescription(descriptionBuilder.build().toString())
                .withCause(e);
    }

     private StorageServiceErrorMeta buildErrorMeta(ServiceException e) {
         return StorageServiceErrorMeta.newBuilder()
                 .setCode(e.getServiceErrorCode())
                 .setMessage(e.getErrorCaption())
                 .build();
     }

    @GrpcExceptionHandler
    public Status handleInternal(Throwable e) {
        log.error("Internal error", e);

        String message = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";

         StorageServiceInternalError.Builder descriptionBuilder =
                 StorageServiceInternalError.newBuilder()
                         .setMeta(StorageServiceErrorMeta.newBuilder()
                                 .setCode(StorageServiceErrorCode.STORAGE_SERVICE_ERROR_CODE_INTERNAL)
                                 .setMessage(message)
                                 .build());

         return Status.INTERNAL
                 .withDescription(descriptionBuilder.build().toString())
                 .withCause(e);
    }
}
