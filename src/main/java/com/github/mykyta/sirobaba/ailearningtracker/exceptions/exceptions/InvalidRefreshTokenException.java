package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Created by Mykyta Sirobaba on 26.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
