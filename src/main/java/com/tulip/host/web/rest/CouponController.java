package com.tulip.host.web.rest;


import com.tulip.host.data.CouponDTO;
import com.tulip.host.service.CouponService;
import com.tulip.host.web.rest.vm.CouponValidationRequestVM;
import com.tulip.host.web.rest.vm.CouponValidationResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @PostMapping
//    public ResponseEntity<CouponDTO> createCoupon(@Valid @RequestBody CouponRequestDTO requestDTO) {
//        return new ResponseEntity<>(couponService.createCoupon(requestDTO), HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<CouponDTO> updateCoupon(
//        @PathVariable Long id,
//        @Valid @RequestBody CouponRequestDTO requestDTO) {
//        return ResponseEntity.ok(couponService.updateCoupon(id, requestDTO));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponValidationResponseVM> validateCoupon(
        @Valid @RequestBody CouponValidationRequestVM requestDTO) {
        return ResponseEntity.ok(couponService.validateCoupon(requestDTO));
    }

}
