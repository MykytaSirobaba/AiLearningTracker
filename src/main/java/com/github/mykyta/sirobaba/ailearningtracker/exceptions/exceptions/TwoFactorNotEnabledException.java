package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Created by Mykyta Sirobaba on 21.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class TwoFactorNotEnabledException extends RuntimeException {
    public TwoFactorNotEnabledException(String message) {
        super(message);
    }
}
