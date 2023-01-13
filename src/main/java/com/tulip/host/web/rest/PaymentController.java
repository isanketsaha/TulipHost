package com.tulip.host.web.rest;

import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.service.PaymentService;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.Date;
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
    public Long pay(@RequestBody PayVM payVM) {
        if (payVM.getPayType().equalsIgnoreCase(PayTypeEnum.FEES.name())) {
            return paymentService.payFees(payVM);
        } else {
            return paymentService.payPurchase(payVM);
        }
    }

    @GetMapping("/details/{paymentType}/{paymentId}")
    public PaySummaryDTO paymentDetails(@Valid @PathVariable Long paymentId, @Valid @PathVariable PayTypeEnum paymentType) {
        if (paymentType == PayTypeEnum.FEES) {
            return paymentService.feesDetail(paymentId);
        } else {
            return paymentService.purchaseDetails(paymentId);
        }
    }
    //    @GetMapping("/history/{from}")
    //    public List<PaySummaryDTO> paymentHistory(@Valid @PathVariable Date from) {
    //
    //    }
}
