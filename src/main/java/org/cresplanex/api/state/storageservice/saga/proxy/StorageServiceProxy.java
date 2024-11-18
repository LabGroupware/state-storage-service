package org.cresplanex.api.state.storageservice.saga.proxy;

import org.cresplanex.api.state.common.saga.SagaCommandChannel;
import org.cresplanex.api.state.common.saga.command.storage.CreateFileObjectCommand;
import org.cresplanex.api.state.common.saga.command.userprofile.CreateUserProfileCommand;
import org.cresplanex.core.saga.simpledsl.CommandEndpoint;
import org.cresplanex.core.saga.simpledsl.CommandEndpointBuilder;
import org.springframework.stereotype.Component;

@Component
public class StorageServiceProxy {

    public final CommandEndpoint<CreateFileObjectCommand.Exec> createFileObject
            = CommandEndpointBuilder
            .forCommand(CreateFileObjectCommand.Exec.class)
            .withChannel(SagaCommandChannel.STORAGE)
            .withCommandType(CreateFileObjectCommand.Exec.TYPE)
            .build();

    public final CommandEndpoint<CreateFileObjectCommand.Undo> undoCreateFileObject
            = CommandEndpointBuilder
            .forCommand(CreateFileObjectCommand.Undo.class)
            .withChannel(SagaCommandChannel.STORAGE)
            .withCommandType(CreateFileObjectCommand.Undo.TYPE)
            .build();
}
