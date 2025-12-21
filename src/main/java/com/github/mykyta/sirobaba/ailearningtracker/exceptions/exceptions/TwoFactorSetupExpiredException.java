package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Created by Mykyta Sirobaba on 21.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class TwoFactorSetupExpiredException extends RuntimeException {
    public TwoFactorSetupExpiredException(String message) {
        super(message);
    }
}
