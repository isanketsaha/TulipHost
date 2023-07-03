package com.tulip.host.data;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class DuesDTO {

    private Long id;

    private Date dueDate;

    private double dueAmount;

    private String approvedBy;

    private List<DuesPaymentDTO> duesPayment;

    private String status;

    private double penalty;

    private Date createdDate;
}
