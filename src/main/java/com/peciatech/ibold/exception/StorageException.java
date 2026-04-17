package com.peciatech.ibold.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends ApiException {
    public StorageException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_ERROR", message);
    }
}
