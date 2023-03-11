package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.PayTypeEnum;
import lombok.Data;

@Data
public class EditOrderVm {

    Long paymentId;
    Long itemId;
    PayTypeEnum payTypeEnum;
}
