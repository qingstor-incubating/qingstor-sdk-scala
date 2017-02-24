package com.qingstor.sdk.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParamAnnotation {
    String location() default "body";

    String name() default "";
}