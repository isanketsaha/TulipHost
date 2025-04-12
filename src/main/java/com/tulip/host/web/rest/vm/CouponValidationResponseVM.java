package com.tulip.host.web.rest.vm;

import com.tulip.host.domain.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponseVM {
    private boolean valid;
    private String code;
    private String message;
    private Coupon.DiscountType discountType;
    private BigDecimal discountValue;
}
