package com.tulip.host.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GalleryImageDTO {

    private String url;
    private String title;
    private int order;
}
