package com.tulip.host.web.rest.vm;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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

    UploadVM duesDocs;
}
