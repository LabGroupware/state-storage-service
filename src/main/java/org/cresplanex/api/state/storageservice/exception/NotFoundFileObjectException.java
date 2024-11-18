package org.cresplanex.api.state.storageservice.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class NotFoundFileObjectException extends RuntimeException {

    private final List<String> fileObjectIds;

    public NotFoundFileObjectException(List<String> fileObjectIds) {
        super("Not found file objects: " + fileObjectIds.stream().reduce((a, b) -> a + ", " + b).orElse(""));
        this.fileObjectIds = fileObjectIds;
    }
}
