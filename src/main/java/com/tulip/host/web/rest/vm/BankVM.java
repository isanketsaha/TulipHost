package com.tulip.host.web.rest.vm;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankVM {

    @NotNull
    String accountNumber;

    @NotNull
    String bankName;

    String confirmAccountNumber;

    @NotNull
    String ifsc;
}
