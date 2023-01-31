package com.tulip.host.mapper;

import io.github.rushuat.ocell.field.ValueConverter;
import org.apache.commons.lang3.StringUtils;

public class DoubleConverter implements ValueConverter<String, Integer> {

    @Override
    public String convertInput(Integer value) {
        if (value != null) {
            return String.valueOf(value);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Integer convertOutput(String value) {
        if (value != null) {
            return Integer.parseInt(value);
        }
        return 0;
    }
}
