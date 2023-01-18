package com.tulip.host.web.rest;

import com.tulip.host.data.PayMonthSummary;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.service.PaymentService;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Long pay(@Valid @RequestBody PayVM payVM) {
        if (payVM.getPayType() == PayTypeEnum.FEES) {
            return paymentService.payFees(payVM);
        } else {
            return paymentService.payPurchase(payVM);
        }
    }

    @GetMapping("/details/{paymentId}")
    public PaySummaryDTO paymentDetails(@Valid @PathVariable Long paymentId) {
        return paymentService.paymentDetails(paymentId);
    }

    @GetMapping("/history/{studentId}")
    public List<PayMonthSummary> history(@Valid @PathVariable Long studentId) {
        return paymentService.yearFeesSummary(2L, studentId);
    }
}
