package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.RelationEnum;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentVM {

    @NotNull
    String aadhaar;

    @NotNull
    String contact;

    @NotNull
    String name;

    String occupation;
    String qualification;

    @NotNull
    RelationEnum relation;

    boolean whatsappAvailable;
}
