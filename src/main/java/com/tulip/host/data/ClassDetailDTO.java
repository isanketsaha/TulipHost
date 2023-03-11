package com.tulip.host.data;

import com.tulip.host.domain.ProductCatalog;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.Size;
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

    private List<CatalogDTO> productCatalogs;
}
