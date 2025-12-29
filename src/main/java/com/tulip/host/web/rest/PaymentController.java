package com.tulip.host.web.rest;

import static com.tulip.host.config.Constants.INVOICE;

import com.tulip.host.data.FeesGraphDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.service.ExportService;
import com.tulip.host.service.PaymentService;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.DuePaymentVm;
import com.tulip.host.web.rest.vm.EditOrderVm;
import com.tulip.host.web.rest.vm.ExpenseVm;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.PayVM;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.xml.bind.ValidationException;

import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ExportService exportService;

    private final UploadService uploadService;

    @PostMapping
    public Long pay(@Valid @RequestBody PayVM payVM)
        throws jakarta.xml.bind.ValidationException {
        Long paymentId;
        if (payVM.getPayType() == PayTypeEnum.FEES) {
            paymentId = paymentService.payFees(payVM);
        } else {
            paymentId = paymentService.payPurchase(payVM);
        }
        CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = exportService.paymentReceipt(paymentId);
                FileUploadVM save = null;
                save = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, INVOICE);
                save.setName("INVOICE-" + paymentId);
                paymentService.attachInvoice(paymentId, save);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return paymentId;
    }

    @GetMapping("/details/{paymentId}")
    public PaySummaryDTO paymentDetails(@Valid @PathVariable Long paymentId) {
        return paymentService.paymentDetails(paymentId);
    }

    @GetMapping("/dues/all")
    public List<PaySummaryDTO> allDues() {
        return paymentService.allDues();
    }

    @PostMapping("/duePayment")
    public long duePayment(@Valid @RequestBody DuePaymentVm duePaymentVm) throws ValidationException, jakarta.xml.bind.ValidationException {
        return paymentService.payDues(duePaymentVm);
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
    public Long registerExpense(@Valid @RequestBody ExpenseVm item) {
        return paymentService.registerExpense(item);
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
