package com.tulip.host.web.rest.vm;


import com.tulip.host.domain.Coupon;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponValidationRequestVM {
    @NotBlank(message = "Coupon code is required")
    private String code;

//    @NotNull(message = "Student Id is required")
//    private Long userId;

}
