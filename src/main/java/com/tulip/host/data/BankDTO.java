package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDTO {

    Long id;

    @NotNull
    String accountNumber;

    @NotNull
    String bankName;

    @NotNull
    String ifsc;
}
