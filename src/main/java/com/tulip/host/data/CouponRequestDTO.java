package com.tulip.host.data;

import com.tulip.host.domain.Coupon;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequestDTO {
    @NotNull
    private String code;
    @NotNull
    private String description;
    @NotNull
    private Coupon.DiscountType discountType;
    @NotNull
    private BigDecimal discountValue;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    private Boolean isActive = true;
    @NotNull
    private Integer usageLimit;
} 