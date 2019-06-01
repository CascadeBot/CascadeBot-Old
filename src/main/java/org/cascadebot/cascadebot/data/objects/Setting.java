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


    /**
     * This is the name that will be used on the web dashboard instead of the
     * field name. TODO: This will need to be localised
     */
    String niceName();


    /**
     * The description of this setting to be displayed on the web dashboard.
     * TODO: This will also need to be localised
     */
    String description() default "";


    /**
     * The flag(s) required to have access to change this setting.
     */
    Flag[] flagRequired() default {};


    /**
     * The module(s) that this setting belongs to. Used to organise the web panel.
     */
    Module[] module() default {};

    /**
     * Whether the user can directly edit this setting through the ;settings command.
     */
    boolean directlyEditable() default true;

}
