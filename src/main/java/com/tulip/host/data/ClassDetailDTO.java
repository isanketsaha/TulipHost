package com.tulip.host.data;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link ClassDetail} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailDTO implements Serializable {

    private Long id;

    @Size(max = 10)
    private String std;

    private String headTeacher;
    private String session;
    private String sessionId;

    private double pendingFees;

    private List<StudentBasicDTO> students;

    private List<FeesCatalogDTO> feesCatalogs;

    private List<ProductDTO> productCatalogs;
}
