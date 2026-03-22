package com.tulip.host.web.rest.vm.dataload;

import com.tulip.host.web.rest.vm.ClassLoadVM;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class UpdateSessionVM {

    private Long sessionId;

    private List<ClassLoadVM> stdList;

    private Map<String, List<FeesLoadVM>> feesList;
}
