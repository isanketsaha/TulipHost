package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DueVM {

    @NotNull
    double dueAmount;

    @NotNull
    String approvedBy;

    @NotNull
    Date paymentDate;

    FileUploadVM duesDocs;
}
