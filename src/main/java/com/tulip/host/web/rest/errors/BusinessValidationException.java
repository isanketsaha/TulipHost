package com.tulip.host.web.rest.errors;

import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

public class BusinessValidationException extends AbstractThrowableProblem {

    private static final String PARAM = "param";

    public BusinessValidationException(String message, String detail) {
        super(ErrorConstants.BUSINESS_TYPE, "Business Exception", BAD_REQUEST, detail, null, null);
    }

    public BusinessValidationException(String message, String detail, Map<String, Object> paramMap) {
        super(ErrorConstants.BUSINESS_TYPE, "Validation Error", BAD_REQUEST, detail, null, null, toProblemParameters(message, paramMap));
    }

    public static Map<String, Object> toParamMap(String... params) {
        Map<String, Object> paramMap = new HashMap<>();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                paramMap.put(PARAM + i, params[i]);
            }
        }
        return paramMap;
    }

    public static Map<String, Object> toProblemParameters(String message, Map<String, Object> paramMap) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", message);
        parameters.put("params", paramMap);
        return parameters;
    }
}
