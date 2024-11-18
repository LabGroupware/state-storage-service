package org.cresplanex.api.state.storageservice.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import org.cresplanex.api.state.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.cresplanex.api.state.common.utils.OriginalAutoGenerate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file_objects",
        indexes = {
                @Index(name = "file_objects_bucket_id_index", columnList = "bucket_id"),
                @Index(name = "file_objects_path_index", columnList = "path"),
                @Index(name = "file_objects_name_index", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "file_objects_bucket_id_path_index", columnNames = {"bucket_id", "path"})
        })
public class FileObjectEntity extends BaseEntity<FileObjectEntity> {

    @Override
    public String getId() {
        return fileObjectId;
    }

    @Override
    public void setId(String id) {
        fileObjectId = id;
    }

    @Id
    @OriginalAutoGenerate
    @Column(name = "file_object_id", length = 100, nullable = false, unique = true)
    private String fileObjectId;

    @Column(name = "bucket_id", length = 100, nullable = false)
    private String bucketId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "path", length = 200, nullable = false)
    private String path;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "mime_type", length = 100, nullable = false)
    private String mimeType;

    @Column(name = "chcksum", length = 100, nullable = false)
    private String checksum;
}
