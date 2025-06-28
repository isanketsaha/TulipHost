package com.tulip.host.web.rest;


import com.tulip.host.data.CouponDTO;
import com.tulip.host.data.CouponRequestDTO;
import com.tulip.host.service.CouponService;
import com.tulip.host.web.rest.vm.CouponValidationRequestVM;
import com.tulip.host.web.rest.vm.CouponValidationResponseVM;
import com.tulip.host.web.rest.vm.GenericFilterVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponDTO> getCouponByCode(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getCouponByCode(code));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('UG_ADMIN') or hasAuthority('UG_PRINCIPAL')")
    @PostMapping
    public ResponseEntity<CouponDTO> createCoupon(@Valid @RequestBody CouponRequestDTO requestDTO) {
        log.info("Attempting to create coupon: {}", requestDTO.getCode());
        return new ResponseEntity<>(couponService.createCoupon(requestDTO), HttpStatus.CREATED);
    }

    @PreAuthorize(" hasAuthority('UG_ADMIN') or hasAuthority('UG_PRINCIPAL')")
    @PutMapping("/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequestDTO requestDTO) {
        log.info("Attempting to update coupon: {}", requestDTO.getCode());
        return ResponseEntity.ok(couponService.updateCoupon(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('UG_ADMIN') or hasAuthority('UG_PRINCIPAL')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<CouponDTO> deactivateCoupon(@PathVariable Long id) {
        log.info("Attempting to deactivate coupon with id: {}", id);
        return ResponseEntity.ok(couponService.deactivateCoupon(id));
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponValidationResponseVM> validateCoupon(
        @Valid @RequestBody CouponValidationRequestVM requestDTO) {
        return ResponseEntity.ok(couponService.validateCoupon(requestDTO));
    }

    // Flexible filter endpoint
    @PostMapping("/filter")
    public ResponseEntity<List<CouponDTO>> filterCoupons(@RequestBody GenericFilterVM filterDTO) {
        log.info("Filtering coupons with filters: {}", filterDTO);
        return ResponseEntity.ok(couponService.filterCoupons(filterDTO));
    }

}
