package com.tulip.host.mapper;

import com.tulip.host.data.CouponDTO;
import com.tulip.host.data.CouponRequestDTO;
import com.tulip.host.domain.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "availableLimit", source = ".", qualifiedByName = "deduceUsage")
    @Mapping(target = "transactionsList", ignore = true) // Ignore transactionsList to avoid circular reference
    CouponDTO toDto(Coupon coupon);

    @Mapping(target = "transactionsList", ignore = true)
    Coupon toEntity(CouponDTO couponDTO);

    @Mapping(target = "transactionsList", ignore = true)
    Coupon toEntity(CouponRequestDTO couponRequestDTO);

    @Mapping(target = "availableLimit", source = ".", qualifiedByName = "deduceUsage")
    List<CouponDTO> toDto(List<Coupon> coupon);

    // Add any additional mapping methods as needed

    @Named("deduceUsage")
    default int deduceUsage(Coupon items) {
        if (items == null)
            return 0;
        int usedCount = items.getTransactionsList() != null ? items.getTransactionsList().size() : 0;
        return items.getUsageLimit() - usedCount;
    }
}
