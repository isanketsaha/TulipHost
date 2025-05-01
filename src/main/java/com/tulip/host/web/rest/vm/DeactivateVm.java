package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivateVm {
    @NotNull
    long id;
    String tc;
    String reason;

    public void setTc(String tc) {
        this.tc = tc.toUpperCase(Locale.ROOT);
    }


}
