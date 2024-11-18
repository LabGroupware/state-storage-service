package org.cresplanex.api.state.storageservice.handler;

import build.buf.gen.storage.v1.*;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.mapper.proto.ProtoMapper;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.cresplanex.api.state.storageservice.service.FileObjectService;

import java.util.List;

@RequiredArgsConstructor
@GrpcService
public class StorageServiceHandler extends StorageServiceGrpc.StorageServiceImplBase {

    private final FileObjectService fileObjectService;

    @Override
    public void findFileObject(FindFileObjectRequest request, StreamObserver<FindFileObjectResponse> responseObserver) {
        FileObjectEntity fileObject = fileObjectService.findById(request.getFileObjectId());

        FileObject fileObjectProto = ProtoMapper.convert(fileObject);
        FindFileObjectResponse response = FindFileObjectResponse.newBuilder()
                .setFileObject(fileObjectProto)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // TODO: pagination + with count
    @Override
    public void getFileObjects(GetFileObjectsRequest request, StreamObserver<GetFileObjectsResponse> responseObserver) {
        List<FileObjectEntity> fileObjects = fileObjectService.get();

        List<FileObject> fileObjectProtos = fileObjects.stream()
                .map(ProtoMapper::convert).toList();
        GetFileObjectsResponse response = GetFileObjectsResponse.newBuilder()
                .addAllFileObjects(fileObjectProtos)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createFileObject(CreateFileObjectRequest request, StreamObserver<CreateFileObjectResponse> responseObserver) {
        String operatorId = request.getOperatorId();
        FileObjectEntity fileObject = new FileObjectEntity();
        fileObject.setBucketId(request.getBucketId());
        fileObject.setName(request.getName());
        fileObject.setPath(request.getPath());

        String jobId = fileObjectService.beginCreate(operatorId, fileObject);
        CreateFileObjectResponse response = CreateFileObjectResponse.newBuilder()
                .setJobId(jobId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
