package com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions;

/**
 * Created by Mykyta Sirobaba on 20.08.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
