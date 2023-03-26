package com.tulip.host.web.rest;

import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.service.PaymentService;
import com.tulip.host.web.rest.vm.EditOrderVm;
import com.tulip.host.web.rest.vm.ExpenseItemVM;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.List;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Long pay(@Valid @RequestBody PayVM payVM) throws ValidationException {
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
        @RequestParam(name = "size", defaultValue = "15") int pageSize
    ) {
        return paymentService.getTransactionHistory(pageNo, studentId, pageSize);
    }

    @GetMapping("/feesgraph/{studentId}/{classId}")
    public FeesGraphDTO feesGraph(@Valid @PathVariable Long studentId, @Valid @PathVariable Long classId) {
        return paymentService.getFeesGraph(studentId, classId);
    }

    @PostMapping("/expense")
    public Long registerExpense(@Valid @RequestBody List<ExpenseItemVM> expenseItems) {
        return paymentService.registerExpense(expenseItems);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('UG_ADMIN')")
    public void editOrder(@Valid @RequestBody EditOrderVm editOrderVm) {
        paymentService.edit(editOrderVm);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('UG_ADMIN')")
    public void deleteTransaction(@Valid @RequestParam long transactionId) {
        paymentService.deleteTransaction(transactionId);
    }
}
