package com.tulip.host.service;


import com.tulip.host.data.CouponDTO;
import com.tulip.host.data.CouponRequestDTO;
import com.tulip.host.domain.Coupon;
import com.tulip.host.domain.Transaction;
import com.tulip.host.exceptions.ResourceNotFoundException;
import com.tulip.host.mapper.CouponMapper;
import com.tulip.host.repository.CouponRepository;
import com.tulip.host.web.rest.vm.CouponValidationRequestVM;
import com.tulip.host.web.rest.vm.CouponValidationResponseVM;
import com.tulip.host.web.rest.vm.PayVM;
import com.tulip.host.web.rest.vm.GenericFilterVM;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponService.class);
    private final CouponRepository couponRepository;
    private final CouponMapper modelMapper;

    @Transactional
    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAllActive()
                .map(modelMapper::toDto)
                .orElse(List.of());
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


    @Transactional
    public CouponDTO createCoupon(CouponRequestDTO requestDTO) {
        // Date validation
        if (requestDTO.getStartDate() == null || requestDTO.getEndDate() == null
                || requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            log.error("Invalid date range for coupon: {}", requestDTO.getCode());
            throw new IllegalArgumentException("End date must be after start date and both dates must be provided");
        }

        // Validate that dates are today or in the future
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        if (requestDTO.getStartDate().isBefore(todayStart)) {
            log.error("Start date must be today or in the future for coupon: {}", requestDTO.getCode());
            throw new IllegalArgumentException("Start date must be today or in the future");
        }
        if (requestDTO.getEndDate().isBefore(todayStart)) {
            log.error("End date must be today or in the future for coupon: {}", requestDTO.getCode());
            throw new IllegalArgumentException("End date must be today or in the future");
        }

        Coupon coupon = modelMapper.toEntity(requestDTO);
        Coupon savedCoupon = couponRepository.save(coupon);
        return modelMapper.toDto(savedCoupon);
    }


    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Transactional
    public CouponDTO deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setIsActive(false);
        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon deactivated: {}", coupon.getCode());
        return modelMapper.toDto(savedCoupon);
    }

    public CouponValidationResponseVM validateCoupon(CouponValidationRequestVM requestDTO) {
        String code = requestDTO.getCode();
//        Long userId = requestDTO.getUserId();

        try {
            // Find valid coupon
            Coupon coupon = couponRepository.findValidCoupon(code, LocalDateTime.now())
                .orElseThrow(() -> new Exception("Coupon is invalid or expired"));



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
            discountAmount = coupon.getDiscountValue();
//
//            // Discount cannot be more than purchase amount
//            if (discountAmount.compareTo(purchaseAmount) > 0) {
//                discountAmount = purchaseAmount;
//            }
        }

        return discountAmount;
    }

    @Transactional
    public CouponDTO updateCoupon(Long id, CouponRequestDTO requestDTO) {
        if (requestDTO.getStartDate() == null || requestDTO.getEndDate() == null
                || requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            log.error("Invalid date range for coupon update: {}", requestDTO.getCode());
            throw new IllegalArgumentException("End date must be after start date and both dates must be provided");
        }

        // Validate that dates are today or in the future
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        if (requestDTO.getStartDate().isBefore(todayStart)) {
            log.error("Start date must be today or in the future for coupon update: {}", requestDTO.getCode());
            throw new IllegalArgumentException("Start date must be today or in the future");
        }
        if (requestDTO.getEndDate().isBefore(todayStart)) {
            log.error("End date must be today or in the future for coupon update: {}", requestDTO.getCode());
            throw new IllegalArgumentException("End date must be today or in the future");
        }

        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        Coupon updatedCoupon = modelMapper.toEntity(requestDTO);
        updatedCoupon.setId(existingCoupon.getId());
        Coupon savedCoupon = couponRepository.save(updatedCoupon);
        return modelMapper.toDto(savedCoupon);
    }

    public List<CouponDTO> filterCoupons(GenericFilterVM filterDTO) {
        GenericSpecification<Coupon> spec = new GenericSpecification<>();
        if (filterDTO != null) {
            Specification<Coupon> filterSpec = spec.filterBy(filterDTO.getFilters());
            Sort sort = Sort.by(
                    filterDTO.getSortDirection().equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    filterDTO.getSortBy());
            return couponRepository.findAll(filterSpec, sort)
                    .stream()
                    .map(modelMapper::toDto)
                    .toList();
        }
        return List.of();
    }
}
