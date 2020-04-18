/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
 

package org.cascadebot.cascadebot.utils;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

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

    public static Color randomColor() {
        return Color.getHSBColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public String randomJoke() throws IOException, IllegalArgumentException {
        JsonElement jsonElement = WebUtils.getJsonFromURL("https://icanhazdadjoke.com/");
        return jsonElement.getAsJsonObject().get("joke").getAsString();
    }

}
