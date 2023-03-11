package com.tulip.host.mapper;

import io.github.rushuat.ocell.field.ValueConverter;
import org.apache.commons.lang3.StringUtils;

public class PriceConverter implements ValueConverter<String, Double> {

    @Override
    public String convertInput(Double value) {
        if (value != null) {
            return String.valueOf(value);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Double convertOutput(String value) {
        if (value != null) {
            return 0.0;
        }
        return 0.0;
    }
}
