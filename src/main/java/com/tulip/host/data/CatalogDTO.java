package com.tulip.host.data;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link Catalog} entity
 */
@Data
public class CatalogDTO implements Serializable {

    private final Long id;

    private final String itemName;

    private final Double price;

    private final String type;

    private final String tag;

    private final String std;

    private final String size;
}
