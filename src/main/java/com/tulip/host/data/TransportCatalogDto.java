package com.tulip.host.data;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportCatalogDto {

    private Long id;

    private String location;

    private double amount;

    private Integer distance;

    private LocalTime pickupTime;
}
