package org.cresplanex.api.state.storageservice.handler;

import build.buf.gen.cresplanex.nova.v1.Count;
import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.storage.v1.*;
import org.cresplanex.api.state.common.entity.ListEntityWithCount;
import org.cresplanex.api.state.common.enums.PaginationType;
import org.cresplanex.api.state.storageservice.entity.FileObjectEntity;
import org.cresplanex.api.state.storageservice.enums.FileObjectSortType;
import org.cresplanex.api.state.storageservice.filter.fileobject.BucketFilter;
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

    @Override
    public void getFileObjects(GetFileObjectsRequest request, StreamObserver<GetFileObjectsResponse> responseObserver) {
        FileObjectSortType sortType = switch (request.getSort().getOrderField()) {
            case FILE_OBJECT_ORDER_FIELD_NAME -> (request.getSort().getOrder() == SortOrder.SORT_ORDER_ASC) ?
                    FileObjectSortType.NAME_ASC : FileObjectSortType.NAME_DESC;
            default -> (request.getSort().getOrder() == SortOrder.SORT_ORDER_ASC) ?
                    FileObjectSortType.CREATED_AT_ASC : FileObjectSortType.CREATED_AT_DESC;
        };
        PaginationType paginationType;
        switch (request.getPagination().getType()) {
            case PAGINATION_TYPE_CURSOR -> paginationType = PaginationType.CURSOR;
            case PAGINATION_TYPE_OFFSET -> paginationType = PaginationType.OFFSET;
            default -> paginationType = PaginationType.NONE;
        }

        BucketFilter bucketFilter = new BucketFilter(
                request.getFilterBucket().getHasValue(), request.getFilterBucket().getBucketIdsList()
        );

        ListEntityWithCount<FileObjectEntity> fileObjects = fileObjectService.get(
                paginationType, request.getPagination().getLimit(), request.getPagination().getOffset(),
                request.getPagination().getCursor(), sortType, request.getWithCount(), bucketFilter);

        List<FileObject> fileObjectProtos = fileObjects.getData().stream()
                .map(ProtoMapper::convert).toList();
        GetFileObjectsResponse response = GetFileObjectsResponse.newBuilder()
                .addAllFileObjects(fileObjectProtos)
                .setCount(
                        Count.newBuilder().setIsValid(request.getWithCount())
                                .setCount(fileObjects.getCount()).build()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPluralFileObjects(GetPluralFileObjectsRequest request, StreamObserver<GetPluralFileObjectsResponse> responseObserver) {
        FileObjectSortType sortType = switch (request.getSort().getOrderField()) {
            case FILE_OBJECT_ORDER_FIELD_NAME -> (request.getSort().getOrder() == SortOrder.SORT_ORDER_ASC) ?
                    FileObjectSortType.NAME_ASC : FileObjectSortType.NAME_DESC;
            default -> (request.getSort().getOrder() == SortOrder.SORT_ORDER_ASC) ?
                    FileObjectSortType.CREATED_AT_ASC : FileObjectSortType.CREATED_AT_DESC;
        };
        List<FileObject> fileObjectProtos = this.fileObjectService.getByFileObjectIds(
                        request.getFileObjectIdsList(), sortType).stream()
                .map(ProtoMapper::convert).toList();
        GetPluralFileObjectsResponse response = GetPluralFileObjectsResponse.newBuilder()
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
