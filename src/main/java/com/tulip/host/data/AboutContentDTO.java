package com.tulip.host.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AboutContentDTO {

    /** Presigned S3 URL for the about section image. Null if not yet uploaded to S3. */
    private String imageUrl;
}
