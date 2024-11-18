package org.cresplanex.api.state.storageservice.event.publisher;

import org.cresplanex.api.state.common.event.EventAggregateType;
import org.cresplanex.api.state.common.event.model.storage.FileObjectDomainEvent;
import org.cresplanex.api.state.common.event.model.userprofile.UserProfileDomainEvent;
import org.cresplanex.api.state.common.event.publisher.AggregateDomainEventPublisher;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.core.events.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class FileObjectDomainEventPublisher extends AggregateDomainEventPublisher<FileObjectEntity, FileObjectDomainEvent> {

    public FileObjectDomainEventPublisher(DomainEventPublisher eventPublisher) {
        super(eventPublisher, FileObjectEntity.class, EventAggregateType.STORAGE_OBJECT);
    }
}
