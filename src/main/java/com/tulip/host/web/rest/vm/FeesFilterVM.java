package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.FeesRuleType;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesFilterVM {

    @NotNull
    List<Long> std;
}
