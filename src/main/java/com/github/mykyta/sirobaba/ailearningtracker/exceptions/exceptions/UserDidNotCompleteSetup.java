package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class UserDidNotCompleteSetup extends RuntimeException {
    public UserDidNotCompleteSetup(String message) {
        super(message);
    }
}
