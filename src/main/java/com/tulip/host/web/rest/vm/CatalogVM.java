package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.StdEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogVM {

    @NotNull
    StdEnum std;

    @NotNull
    Long session;
}
