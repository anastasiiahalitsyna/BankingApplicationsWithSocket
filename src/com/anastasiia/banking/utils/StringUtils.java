package com.anastasiia.banking.utils;

public class StringUtils {

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static void ifBlankThenThrow(String value, RuntimeException runtimeException) {
        if (!isNotBlank(value)) {
            throw runtimeException;
        }
    }

}
