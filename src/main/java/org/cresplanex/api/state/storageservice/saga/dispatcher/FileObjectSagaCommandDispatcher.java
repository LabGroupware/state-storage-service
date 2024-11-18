package org.cresplanex.api.state.storageservice.saga.dispatcher;

import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.saga.SagaCommandChannel;
import org.cresplanex.api.state.common.saga.dispatcher.BaseSagaCommandDispatcher;
import org.cresplanex.api.state.storageservice.saga.handler.FileObjectSagaCommandHandlers;
import org.cresplanex.core.saga.participant.SagaCommandDispatcher;
import org.cresplanex.core.saga.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class FileObjectSagaCommandDispatcher extends BaseSagaCommandDispatcher {

    @Bean
    public SagaCommandDispatcher fileObjectSagaCommandHandlersDispatcher(
            FileObjectSagaCommandHandlers fileObjectSagaCommandHandlers,
            SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(
                this.getDispatcherId(SagaCommandChannel.STORAGE, "FileObject"),
                fileObjectSagaCommandHandlers.commandHandlers());
    }
}
