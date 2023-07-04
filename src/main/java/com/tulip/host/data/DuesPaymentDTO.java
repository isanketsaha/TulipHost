package com.tulip.host.data;

import com.tulip.host.enums.PaymentOptionEnum;
import java.util.Date;
import lombok.Data;

@Data
public class DuesPaymentDTO {

    private Long id;
    private double amount;
    private double penalty;
    private PaymentOptionEnum paymentMode;
    private Date createdDate;
}
