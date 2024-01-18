package com.tulip.host.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadVM {

    private Long id;

    @JsonProperty("response")
    private String uid;

    private String name;
    private String status;
    private String type;
    private long size;
    private String documentType;
    private LocalDateTime lastModifiedDate;
}
