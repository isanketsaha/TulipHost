package com.tulip.host.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityItemDTO {

    private String imageUrl;
    private String title;
    private String description;
    private String date;
}
