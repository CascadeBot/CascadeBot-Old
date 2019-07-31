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
import org.cascadebot.cascadebot.data.objects.donation.Flag;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {


    /**
     * The flag(s) required to have access to change this setting.
     */
    //Flag[] flagRequired() default {};


    /**
     * The module(s) that this setting belongs to. Used to organise the web panel.
     */
    Module[] module() default {};

    /**
     * Whether the user can directly edit this setting through the ;settings command.
     */
    boolean directlyEditable() default true;

}
