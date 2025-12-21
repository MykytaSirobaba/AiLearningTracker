package com.github.mykyta.sirobaba.ailearningtracker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Mykyta Sirobaba on 29.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CurrentUser {
}
