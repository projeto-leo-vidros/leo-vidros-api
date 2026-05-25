package com.project.extension.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        Object details
) {
    public ApiError(String code, String message) {
        this(code, message, null);
    }
}
