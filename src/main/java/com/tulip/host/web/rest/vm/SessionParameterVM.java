package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionParameterVM {

    @NotNull
    private Long sessionId;

    @NotNull
    private List<Long> parameterIds;
}
