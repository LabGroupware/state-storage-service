package org.cresplanex.api.state.storageservice.saga.state.userprofile;

import lombok.*;
import org.cresplanex.api.state.common.dto.storage.FileObjectDto;
import org.cresplanex.api.state.common.dto.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.common.saga.command.storage.CreateFileObjectCommand;
import org.cresplanex.api.state.common.saga.state.SagaState;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.saga.model.userprofile.CreateFileObjectSaga;

@Setter
@Getter
@NoArgsConstructor
public class CreateFileObjectSagaState
        extends SagaState<CreateFileObjectSaga.Action, FileObjectEntity> {
    private InitialData initialData;
    private FileObjectDto fileObjectDto = FileObjectDto.empty();
    private String operatorId;

    @Override
    public String getId() {
        return fileObjectDto.getFileObjectId();
    }

    @Override
    public Class<FileObjectEntity> getEntityClass() {
        return FileObjectEntity.class;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InitialData {
        private String bucketId;
        private String name;
        private String path;
    }

    public CreateFileObjectCommand.Exec makeCreateFileObjectCommand() {
        return new CreateFileObjectCommand.Exec(
                this.operatorId,
                initialData.getBucketId(),
                initialData.getName(),
                initialData.getPath()
        );
    }

    public CreateFileObjectCommand.Undo makeUndoCreateFileObjectCommand() {
        return new CreateFileObjectCommand.Undo(
                fileObjectDto.getFileObjectId()
        );
    }
}
