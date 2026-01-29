package com.tulip.host.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Custom error decoder for Feign client responses.
 * Handles HTTP error responses and converts them into appropriate exceptions.
 */
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(FeignErrorDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        LOG.error("Feign client error - Method: {}, Status: {}, Reason: {}", methodKey, response.status(), response.reason());

        if (response.status() == 0) {
            return new FeignException("Service unavailable", response);
        }

        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        if (httpStatus != null) {
            switch (httpStatus) {
                case UNAUTHORIZED:
                    LOG.error("Unauthorized access for method: {}", methodKey);
                    return new FeignUnauthorizedException("Unauthorized request", response);
                case FORBIDDEN:
                    LOG.error("Forbidden access for method: {}", methodKey);
                    return new FeignForbiddenException("Forbidden request", response);
                case NOT_FOUND:
                    LOG.error("Resource not found for method: {}", methodKey);
                    return new FeignNotFoundException("Resource not found", response);
                case INTERNAL_SERVER_ERROR:
                    LOG.error("Internal server error for method: {}", methodKey);
                    return new FeignServerException("Internal server error", response);
                case BAD_REQUEST:
                    LOG.error("Bad request for method: {}", methodKey);
                    return new FeignBadRequestException("Bad request", response);
                case SERVICE_UNAVAILABLE:
                    LOG.error("Service unavailable for method: {}", methodKey);
                    return new FeignServiceUnavailableException("Service unavailable", response);
                default:
                    LOG.error("Client error for method: {} with status: {}", methodKey, response.status());
                    return new FeignException("Client error: " + response.reason(), response);
            }
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    // Custom exception classes
    public static class FeignException extends RuntimeException {

        private final Response response;

        public FeignException(String message, Response response) {
            super(message);
            this.response = response;
        }

        public Response getResponse() {
            return response;
        }

        public int getStatus() {
            return response.status();
        }
    }

    public static class FeignUnauthorizedException extends FeignException {

        public FeignUnauthorizedException(String message, Response response) {
            super(message, response);
        }
    }

    public static class FeignForbiddenException extends FeignException {

        public FeignForbiddenException(String message, Response response) {
            super(message, response);
        }
    }

    public static class FeignNotFoundException extends FeignException {

        public FeignNotFoundException(String message, Response response) {
            super(message, response);
        }
    }

    public static class FeignServerException extends FeignException {

        public FeignServerException(String message, Response response) {
            super(message, response);
        }
    }

    public static class FeignBadRequestException extends FeignException {

        public FeignBadRequestException(String message, Response response) {
            super(message, response);
        }
    }

    public static class FeignServiceUnavailableException extends FeignException {

        public FeignServiceUnavailableException(String message, Response response) {
            super(message, response);
        }
    }
}
