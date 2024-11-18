CREATE TABLE file_objects (
        file_object_id VARCHAR(100) PRIMARY KEY,
        bucket_id VARCHAR(100) NOT NULL,
        version INTEGER DEFAULT 0 NOT NULL,
        name VARCHAR(200) NOT NULL,
        path VARCHAR(200) NOT NULL,
        size BIGINT,
        mime_type VARCHAR(100),
        chcksum VARCHAR(100),
        created_at TIMESTAMP NOT NULL,
        created_by varchar(50) NOT NULL,
        updated_at TIMESTAMP DEFAULT NULL,
        updated_by varchar(50) DEFAULT NULL
);

CREATE INDEX file_objects_bucket_id_index ON file_objects (bucket_id);
CREATE INDEX file_objects_path_index ON file_objects (path);
CREATE INDEX file_objects_name_index ON file_objects (name);

CREATE UNIQUE INDEX file_objects_bucket_id_path_index ON file_objects (bucket_id, path);