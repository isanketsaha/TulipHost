package com.tulip.host.web.rest;

import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.service.PaymentService;
import com.tulip.host.web.rest.vm.PayVM;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
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
    public PageImpl<PaySummaryDTO> history(
        @Valid @PathVariable Long studentId,
        @RequestParam(value = "page", defaultValue = "0") int pageNo,
        @RequestParam(name = "size", defaultValue = "10") int pageSize
    ) {
        return paymentService.getTransactionHistory(pageNo, studentId, pageSize);
    }

    @GetMapping("/feesgraph/{studentId}/{classId}")
    public FeesGraphDTO feesGraph(@Valid @PathVariable Long studentId, @Valid @PathVariable Long classId) {
        return paymentService.getFeesGraph(studentId, classId);
    }
}
