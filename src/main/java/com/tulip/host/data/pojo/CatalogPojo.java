package com.tulip.host.data.pojo;

import com.tulip.host.domain.Catalog;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link Catalog} entity
 */
@Data
public class CatalogPojo implements Serializable {

    private final Long id;

    @Size(max = 255)
    @NotNull
    private final String itemName;

    @NotNull
    private final Double sellPrice;

    @Size(max = 255)
    private final String description;

    @Size(max = 255)
    @NotNull
    private final String type;

    @Size(max = 255)
    @NotNull
    private final String tag;

    private final ClassDetailPojo std;

    @Size(max = 20)
    private final String size;
}
