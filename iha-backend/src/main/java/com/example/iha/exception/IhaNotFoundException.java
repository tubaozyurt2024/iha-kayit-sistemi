package com.example.iha.exception;

public class IhaNotFoundException extends RuntimeException {

    public IhaNotFoundException(Long id) {
        super("İHA bulunamadı: id=" + id);
    }
}
