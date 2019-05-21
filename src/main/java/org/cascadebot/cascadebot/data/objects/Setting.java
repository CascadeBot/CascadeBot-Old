/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import org.cascadebot.cascadebot.commandmeta.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {

    String niceName();

    String description() default "";

    Flag[] flagRequired() default {};

    Module[] module() default {};

    boolean directlyEditable() default true;

}
