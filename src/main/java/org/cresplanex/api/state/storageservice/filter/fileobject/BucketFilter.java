package org.cresplanex.api.state.storageservice.filter.fileobject;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BucketFilter {

    private boolean isValid;
    private List<String> bucketIds;
}
