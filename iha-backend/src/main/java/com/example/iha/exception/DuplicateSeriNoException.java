package com.example.iha.exception;

public class DuplicateSeriNoException extends RuntimeException {

    public DuplicateSeriNoException(String seriNo) {
        super("Bu seri numarası zaten kayıtlı: " + seriNo);
    }
}
