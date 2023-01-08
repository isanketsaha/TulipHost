package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeesCatalog {

    private Long id;
    private String name;
    private Double amount;
    private String description;
    private String applicableRule;
    private String std;
}
