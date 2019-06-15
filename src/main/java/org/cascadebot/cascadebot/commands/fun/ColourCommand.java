/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.awt.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColourCommand implements ICommandMain {

    private static final Pattern HEX_COLOR = Pattern.compile("#?([A-Fa-f0-9]+)");
    private static final Pattern RGB = Pattern.compile("(\\d{1,3}),(\\d{1,3}),(\\d{1,3})");

    private String getHex(int r, int g, int b) {
        return String.format("%02x%02x%02x", r, g, b);
    }

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String text = context.getArgs()[0];
        Color color = null;
        Matcher matcher;
        String hex;

        try {
            color = (Color) Color.class.getField(text.toUpperCase()).get(null);
            hex = getHex(color.getRed(), color.getBlue(), color.getGreen());
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            if ((matcher = RGB.matcher(text)).find()) {
                hex = getHex(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)));

                if (hex.length() > 6) {
                    context.getTypedMessaging().replyWarning("Values should be between 0-255");
                    return;
                }
            }
            else if ((matcher = HEX_COLOR.matcher(text)).find()) {
                hex = matcher.group();
                if (hex.charAt(0) == '#')
                    hex = hex.substring(1);

                if (hex.length() != 6) {
                    context.getTypedMessaging().replyWarning("Hex code has to be 6 digits. (" + hex.length() + ")");
                    return;
                }

            }
            else
            {
                context.getTypedMessaging().replyWarning("Could not recognise colour from the given value");
                return;
            }
            color = Color.decode('#' + hex);
        }

        int unsignedInt = Integer.parseUnsignedInt(hex, 16);
        String binary = Integer.toBinaryString(unsignedInt);
        context.getTypedMessaging().replyInfo("RGB " + color.getRed() + " " + color.getGreen() + " " + color.getBlue());
        context.getTypedMessaging().replyInfo("Numbers " + String.format("Binary: %s\nDecimal: %d", Integer.toBinaryString(unsignedInt), unsignedInt), true);

    }

    @Override
    public Module getModule() {
        return Module.FUN;
    }


    @Override
    public String command() {
        return "colour";
    }

    @Override
    public Set<String> getGlobalAliases() {
        return java.util.Set.of("color");
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("colour command", "colour", true);
    }

    @Override
    public String description() {
        return "Returns the values of the given colour";
    }

}
