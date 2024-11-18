package org.cresplanex.api.state.storageservice.mapper.proto;

import build.buf.gen.storage.v1.FileObject;
import org.cresplanex.api.state.common.utils.ValueFromNullable;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;

public class ProtoMapper {

    public static FileObject convert(FileObjectEntity fileObjectEntity) {

        return FileObject.newBuilder()
                .setFileObjectId(fileObjectEntity.getFileObjectId())
                .setBucketId(fileObjectEntity.getBucketId())
                .setName(fileObjectEntity.getName())
                .setPath(fileObjectEntity.getPath())
                .setMimeType(ValueFromNullable.toNullableString(fileObjectEntity.getMimeType()))
                .setSize(ValueFromNullable.toNullableInt(fileObjectEntity.getSize()))
                .setChecksum(ValueFromNullable.toNullableString(fileObjectEntity.getChecksum()))
                .build();
    }
}
