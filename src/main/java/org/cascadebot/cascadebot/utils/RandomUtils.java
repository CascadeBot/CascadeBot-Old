/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
 
package org.cascadebot.cascadebot.utils;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {
    
    @Getter
    private static final Random random = new Random();

    @Getter
    private static final String[] characters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

    public static int randomNumber(int Args) {
        return(random.nextInt(Args));
    }
    
    public static String randomChoice(String... choices) {
        return (choices[random.nextInt(choices.length)]);
    }

    public static String randomColor() {
        StringBuilder result = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            result.append(randomChoice(characters));
        }
        return result.toString();
    }

}
