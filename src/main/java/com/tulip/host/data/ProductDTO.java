package com.tulip.host.data;

import java.io.Serializable;

import javax.xml.catalog.Catalog;

import lombok.Data;

/**
 * A DTO for the {@link Catalog} entity
 */
@Data
public class ProductDTO implements Serializable {

    private Long id;

    private String itemName;

    private Double price;

    private String type;

    private String tag;

    private String std;

    private String size;

    private Integer availableStock;
    private Boolean lowStock;
    private String category;
}
