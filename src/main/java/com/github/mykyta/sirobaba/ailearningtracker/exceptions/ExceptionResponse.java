package com.github.mykyta.sirobaba.ailearningtracker.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
