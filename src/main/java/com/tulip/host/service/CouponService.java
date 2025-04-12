package com.tulip.host.service;


import com.tulip.host.data.CouponDTO;
import com.tulip.host.domain.Coupon;
import com.tulip.host.domain.Transaction;
import com.tulip.host.exceptions.ResourceNotFoundException;
import com.tulip.host.mapper.CouponMapper;
import com.tulip.host.repository.CouponRepository;
import com.tulip.host.web.rest.vm.CouponValidationRequestVM;
import com.tulip.host.web.rest.vm.CouponValidationResponseVM;
import com.tulip.host.web.rest.vm.PayVM;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponMapper modelMapper;

    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAll()
            .stream()
            .map(coupon -> modelMapper.toDto(coupon))
            .collect(Collectors.toList());
    }


    public CouponDTO getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        return modelMapper.toDto(coupon);
    }


    public CouponDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return modelMapper.toDto(coupon);
    }


//    @Transactional
//    public CouponDTO createCoupon(CouponRequestDTO requestDTO) {
//        validateCouponDates(requestDTO);
//
//        Coupon coupon = modelMapper.map(requestDTO, Coupon.class);
//        coupon.setUsedCount(0);
//
//        Coupon savedCoupon = couponRepository.save(coupon);
//        return modelMapper.map(savedCoupon, CouponDTO.class);
//    }
//
//
//    @Transactional
//    public CouponDTO updateCoupon(Long id, CouponRequestDTO requestDTO) {
//        validateCouponDates(requestDTO);
//
//        Coupon existingCoupon = couponRepository.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
//
//        modelMapper.map(requestDTO, existingCoupon);
//        Coupon updatedCoupon = couponRepository.save(existingCoupon);
//
//        return modelMapper.map(updatedCoupon, CouponDTO.class);
//    }


    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }


    public CouponValidationResponseVM validateCoupon(CouponValidationRequestVM requestDTO) {
        String code = requestDTO.getCode();
//        Long userId = requestDTO.getUserId();

        try {
            // Find valid coupon
            Coupon coupon = couponRepository.findValidCoupon(code, LocalDateTime.now())
                .orElseThrow(() -> new Exception("Coupon is invalid or expired"));

            // Check if minimum purchase amount is met
//            if (coupon.getMinPurchaseAmount() != null &&
//                purchaseAmount.compareTo(coupon.getMinPurchaseAmount()) < 0) {
//                return CouponValidationResponseVM.builder()
//                    .valid(false)
//                    .code(code)
//                    .message("Minimum purchase amount not met. Required: " + coupon.getMinPurchaseAmount())
//                    .build();
//            }
//
//            // Check if user has already used this coupon (if userId provided)
//            if (userId != null) {
//                int usageCount = couponUsageRepository.countByCouponAndUserId(coupon, userId);
//                if (usageCount > 0) {
//                    return CouponValidationResponseDTO.builder()
//                        .valid(false)
//                        .code(code)
//                        .message("You have already used this coupon")
//                        .build();
//                }
//            }

            // Calculate discount
//            BigDecimal discountAmount = calculateDiscount(coupon, purchaseAmount);
//            BigDecimal finalAmount = purchaseAmount.subtract(discountAmount);

            return CouponValidationResponseVM.builder()
                .valid(true)
                .code(code)
                .message("Coupon applied successfully")
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .build();

        } catch (Exception e) {
            return CouponValidationResponseVM.builder()
                .valid(false)
                .code(code)
                .message(e.getMessage())
                .build();
        }
    }


    @Transactional
    public void applyCoupon(Transaction transaction, PayVM vm) {
        Coupon coupon = couponRepository.findValidCoupon(vm.getCouponCode(), LocalDateTime.now())
            .orElseThrow(() -> new ResourceNotFoundException("Coupon is invalid or expired"));
        BigDecimal discountAmount = calculateDiscount(coupon, BigDecimal.valueOf(vm.getSubTotal()));
        if (discountAmount.compareTo(BigDecimal.valueOf(vm.getDiscountAmount())) == 0) {
            transaction.setCouponId(coupon);
            couponRepository.save(coupon);
        } else {
            throw new IllegalArgumentException("Invalid coupon code or mismatch discount amount");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal purchaseAmount) {
        BigDecimal discountAmount;

        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            // Calculate percentage discount
            discountAmount = purchaseAmount.multiply(coupon.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

//            // Apply max discount if specified
//            if (coupon.getMaxDiscountAmount() != null &&
//                discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
//                discountAmount = coupon.getMaxDiscountAmount();
//            }
        } else {
            // Fixed amount discount
            discountAmount = coupon.getDiscountValue();
//
//            // Discount cannot be more than purchase amount
//            if (discountAmount.compareTo(purchaseAmount) > 0) {
//                discountAmount = purchaseAmount;
//            }
        }

        return discountAmount;
    }

//    private void validateCouponDates(CouponRequestDTO requestDTO) {
//        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
//            throw new IllegalArgumentException("Start date must be before end date");
//        }
//
//        if (requestDTO.getEndDate().isBefore(LocalDateTime.now())) {
//            throw new IllegalArgumentException("End date must be in the future");
//        }
//    }
}
