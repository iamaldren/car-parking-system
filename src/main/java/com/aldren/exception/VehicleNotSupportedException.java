package com.aldren.exception;

import lombok.Getter;

@Getter
public class VehicleNotSupportedException extends Exception {

    private String message;

    public VehicleNotSupportedException(String message) {
        this.message = message;
    }

}
