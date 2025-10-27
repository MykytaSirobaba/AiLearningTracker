package com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions;

/**
 * Created by Mykyta Sirobaba on 27.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class UserIsAlreadyRegistered extends RuntimeException {
    public UserIsAlreadyRegistered(String message) {
        super(message);
    }
}
