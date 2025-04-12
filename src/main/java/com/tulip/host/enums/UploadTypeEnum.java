package com.tulip.host.enums;

import com.tulip.host.web.rest.vm.dataload.CalenderLoadVM;
import com.tulip.host.web.rest.vm.dataload.DataLoadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum UploadTypeEnum {
    PRODUCT(ProductLoadVM.class),

    FEES(FeesLoadVM.class),

    CALENDER(CalenderLoadVM.class);

    private final Class<? extends Object> format;

    UploadTypeEnum(Class<? extends Object> format) {
        this.format = format;
    }
}
