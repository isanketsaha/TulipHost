package com.tulip.host.service;

import com.tulip.host.domain.FeesOrder;
import com.tulip.host.domain.PurchaseOrder;
import com.tulip.host.mapper.FeesOrderMapper;
import com.tulip.host.mapper.PurchaseOrderMapper;
import com.tulip.host.repository.FeesOrderRepository;
import com.tulip.host.repository.PurchaseOrderRepository;
import com.tulip.host.web.rest.vm.PayVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FeesOrderRepository feesOrderRepository;
    private final FeesOrderMapper feesOrderMapper;

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final PurchaseOrderRepository purchaseOrderRepository;

    public Long payFees(PayVM payVM) {
        FeesOrder modelFromEntity = feesOrderMapper.getModelFromEntity(payVM);
        modelFromEntity
            .getLineItem()
            .forEach(item -> {
                item.setFeesOrder(modelFromEntity);
            });
        FeesOrder save = feesOrderRepository.saveAndFlush(modelFromEntity);
        return save.getId();
    }

    public Long payPurchase(PayVM payVM) {
        PurchaseOrder modelFromEntity = purchaseOrderMapper.toModel(payVM);
        modelFromEntity
            .getLineItem()
            .forEach(item -> {
                item.setPurchaseOrder(modelFromEntity);
            });
        PurchaseOrder purchaseOrder = purchaseOrderRepository.saveAndFlush(modelFromEntity);
        return purchaseOrder.getId();
    }
}
