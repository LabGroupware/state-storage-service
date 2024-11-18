package org.cresplanex.api.state.storageservice.mapper.dto;

import org.cresplanex.api.state.common.dto.storage.FileObjectDto;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;

public class DtoMapper {

    public static FileObjectDto convert(FileObjectEntity fileObjectEntity) {
        return FileObjectDto.builder()
                .fileObjectId(fileObjectEntity.getFileObjectId())
                .bucketId(fileObjectEntity.getBucketId())
                .name(fileObjectEntity.getName())
                .path(fileObjectEntity.getPath())
                .mimeType(fileObjectEntity.getMimeType())
                .size(fileObjectEntity.getSize())
                .checksum(fileObjectEntity.getChecksum())
                .build();
    }
}
