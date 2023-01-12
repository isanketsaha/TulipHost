package com.tulip.host.web.rest;

import com.tulip.host.service.PaymentService;
import com.tulip.host.web.rest.vm.PayVM;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Long pay(@RequestBody PayVM payVM) {
        if (payVM.getPayType().equalsIgnoreCase("FEES")) {
            return paymentService.payFees(payVM);
        } else {
            return paymentService.payPurchase(payVM);
        }
    }
}
