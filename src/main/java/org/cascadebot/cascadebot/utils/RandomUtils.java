/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
 
package org.cascadebot.cascadebot.utils;
 
import java.util.Random;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {
    
    @Getter
    private static final Random random = new Random();

    public static int randomNumber(int Args) {
        return(random.nextInt(Args));
    }
    
    public static String randomChoice(String... choices) {
        int randomLength = choices.length;
        int randomMax = random.nextInt(randomLength);
        return (choices[randomMax]);
    }
}
