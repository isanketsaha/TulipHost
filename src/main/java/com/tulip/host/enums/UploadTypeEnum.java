package com.tulip.host.enums;

import com.tulip.host.web.rest.vm.dataload.DataLoadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum UploadTypeEnum {
    PRODUCT(ProductLoadVM.class),

    FEES(FeesLoadVM.class);

    private final Class<? extends DataLoadVM> format;

    UploadTypeEnum(Class<? extends DataLoadVM> format) {
        this.format = format;
    }
}
