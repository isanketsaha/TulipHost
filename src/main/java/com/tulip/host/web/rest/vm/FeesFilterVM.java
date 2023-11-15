package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.FeesRuleType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
