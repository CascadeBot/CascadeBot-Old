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
        int randomOutput = random.nextInt(Args);
        return randomOutput;
    }
    
    @Getter
    private static final Random random2 = new Random();
    public static String randomChoice(String [] randomArray) {
        int randomLength = randomArray.length;
        if (randomLength <= 1) {
            throw new ArrayIndexOutOfBoundsException("Too short");
        }
        else {
            int randomMax = random2.nextInt(randomLength);
            return (randomArray[randomMax]);
        }
    }
}
