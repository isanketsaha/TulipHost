package com.tulip.host.mapper;

import com.tulip.host.data.CouponDTO;
import com.tulip.host.domain.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CouponMapper {

    // Define mapping methods here
    // For example:
     CouponDTO toDto(Coupon coupon);
     Coupon toEntity(CouponDTO couponDTO);

    // Add any additional mapping methods as needed
}
