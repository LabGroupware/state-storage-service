package org.cresplanex.api.state.storageservice.exception;

import build.buf.gen.storage.v1.StorageServiceErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StorageNotFoundException extends ServiceException {

    private final FindType findType;
    private final String aggregateId;

    public StorageNotFoundException(FindType findType, String aggregateId) {
        this(findType, aggregateId, "Model not found: " + findType.name() + " with id " + aggregateId);
    }

    public StorageNotFoundException(FindType findType, String aggregateId, String message) {
        super(message);
        this.findType = findType;
        this.aggregateId = aggregateId;
    }

    public StorageNotFoundException(FindType findType, String aggregateId, String message, Throwable cause) {
        super(message, cause);
        this.findType = findType;
        this.aggregateId = aggregateId;
    }

    public enum FindType {
        BY_ID
    }

    @Override
    public StorageServiceErrorCode getServiceErrorCode() {
        return StorageServiceErrorCode.STORAGE_SERVICE_ERROR_CODE_STORAGE_NOT_FOUND;
    }

    @Override
    public String getErrorCaption() {
        return switch (findType) {
            case BY_ID -> "User Storage not found (ID = %s)".formatted(aggregateId);
            default -> "User Storage not found";
        };
    }
}
