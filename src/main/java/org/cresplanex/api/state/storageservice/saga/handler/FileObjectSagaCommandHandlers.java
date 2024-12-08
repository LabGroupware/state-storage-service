package org.cresplanex.api.state.storageservice.saga.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.StorageServiceApplicationCode;
import org.cresplanex.api.state.common.saga.LockTargetType;
import org.cresplanex.api.state.common.saga.SagaCommandChannel;
import org.cresplanex.api.state.common.saga.command.storage.CreateFileObjectCommand;
import org.cresplanex.api.state.common.saga.reply.storage.CreateFileObjectReply;
import org.cresplanex.api.state.common.saga.reply.storage.FileObjectExistValidateReply;
import org.cresplanex.api.state.common.saga.validate.storage.FileObjectExistValidateCommand;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.exception.NotFoundFileObjectException;
import org.cresplanex.api.state.storageservice.mapper.dto.DtoMapper;
import org.cresplanex.api.state.storageservice.service.FileObjectService;
import org.cresplanex.core.commands.consumer.CommandHandlers;
import org.cresplanex.core.commands.consumer.CommandMessage;
import org.cresplanex.core.commands.consumer.PathVariables;
import org.cresplanex.core.messaging.common.Message;
import org.cresplanex.core.saga.lock.LockTarget;
import org.cresplanex.core.saga.participant.SagaCommandHandlersBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.cresplanex.core.commands.consumer.CommandHandlerReplyBuilder.*;
import static org.cresplanex.core.saga.participant.SagaReplyMessageBuilder.withLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileObjectSagaCommandHandlers {

    private final FileObjectService fileObjectService;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(SagaCommandChannel.STORAGE)
                .onMessage(CreateFileObjectCommand.Exec.class,
                        CreateFileObjectCommand.Exec.TYPE,
                        this::handleCreateFileObjectCommand
                )
                .onMessage(CreateFileObjectCommand.Undo.class,
                        CreateFileObjectCommand.Undo.TYPE,
                        this::handleUndoCreateFileObjectCommand
                )
                .withPreLock(this::undoCreateFileObjectPreLock)

                .onMessage(FileObjectExistValidateCommand.class,
                        FileObjectExistValidateCommand.TYPE,
                        this::handleFileObjectExistValidateCommand
                )
                .build();
    }

    private LockTarget undoCreateFileObjectPreLock(
            CommandMessage<CreateFileObjectCommand.Undo> cmd, PathVariables pvs) {
        return new LockTarget(LockTargetType.STORAGE_OBJECT, cmd.getCommand().getFileObjectId());
    }

    private Message handleCreateFileObjectCommand(CommandMessage<CreateFileObjectCommand.Exec> cmd) {
        try {
            CreateFileObjectCommand.Exec command = cmd.getCommand();
            FileObjectEntity fileObject = new FileObjectEntity();
            fileObject.setName(command.getName());
            fileObject.setPath(command.getPath());
            fileObject.setBucketId(command.getBucketId());
            fileObject = fileObjectService.create(command.getOperatorId(), fileObject);
            CreateFileObjectReply.Success reply = new CreateFileObjectReply.Success(
                    new CreateFileObjectReply.Success.Data(DtoMapper.convert(fileObject)),
                    StorageServiceApplicationCode.SUCCESS,
                    "File object created successfully",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            return withLock(LockTargetType.STORAGE_OBJECT, fileObject.getFileObjectId())
                    .withSuccess(reply, CreateFileObjectReply.Success.TYPE);
        } catch (Exception e) {
            log.error("Failed to create file object", e);
            CreateFileObjectReply.Failure reply = new CreateFileObjectReply.Failure(
                    null,
                    StorageServiceApplicationCode.INTERNAL_SERVER_ERROR,
                    "Failed to create file object",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            return withException(reply, CreateFileObjectReply.Failure.TYPE);
        }
    }

    private Message handleUndoCreateFileObjectCommand(
            CommandMessage<CreateFileObjectCommand.Undo> cmd) {
        try {
            CreateFileObjectCommand.Undo command = cmd.getCommand();
            String fileObjectId = command.getFileObjectId();
            fileObjectService.undoCreate(fileObjectId);
            return withSuccess();
        } catch (Exception e) {
            return withException();
        }
    }

    private Message handleFileObjectExistValidateCommand(
            CommandMessage<FileObjectExistValidateCommand> cmd) {
        try {
            FileObjectExistValidateCommand command = cmd.getCommand();
            fileObjectService.validateFileObjects(command.getFileObjectIds());
            return withSuccess(
                    new FileObjectExistValidateReply.Success(
                            null,
                            StorageServiceApplicationCode.SUCCESS,
                            "File object exist validate successfully",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    ),
                    FileObjectExistValidateReply.Success.TYPE
            );
        } catch (NotFoundFileObjectException e) {
            FileObjectExistValidateReply.Failure reply = new FileObjectExistValidateReply.Failure(
                    new FileObjectExistValidateReply.Failure.FileObjectNotFound(e.getFileObjectIds()),
                    StorageServiceApplicationCode.FILE_OBJECT_NOT_FOUND,
                    "File object not found",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            return withException(reply, FileObjectExistValidateReply.Failure.TYPE);
        }catch (Exception e) {
            FileObjectExistValidateReply.Failure reply = new FileObjectExistValidateReply.Failure(
                    null,
                    StorageServiceApplicationCode.INTERNAL_SERVER_ERROR,
                    "Failed to validate file object",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            return withException(reply, FileObjectExistValidateReply.Failure.TYPE);
        }
    }
}
