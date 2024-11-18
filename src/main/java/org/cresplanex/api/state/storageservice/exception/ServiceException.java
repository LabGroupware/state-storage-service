package org.cresplanex.api.state.storageservice.exception;

import build.buf.gen.storage.v1.StorageServiceErrorCode;

public abstract class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract public StorageServiceErrorCode getServiceErrorCode();
    abstract public String getErrorCaption();
}
