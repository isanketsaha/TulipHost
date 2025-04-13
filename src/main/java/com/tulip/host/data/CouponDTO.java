package com.tulip.host.data;

import com.tulip.host.domain.Coupon;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponDTO {
    private Long id;
    private String code;
    private String description;
    private Coupon.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Integer usageLimit;
    private Integer usedCount;
}
