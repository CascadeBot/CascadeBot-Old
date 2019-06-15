/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */


package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.awt.Color;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColourCommand implements ICommandMain {

    private static final Pattern HEX_COLOR = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
    private static final Pattern RGB = Pattern.compile("(\\d{1,3}),(\\d{1,3}),(\\d{1,3})");

    private String getHex(int r, int g, int b) {
        return String.format("%02x%02x%02x", r, g, b);
    }

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage(this);
            return;
        }
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
            } else if ((matcher = HEX_COLOR.matcher(text)).matches()) {
                hex = matcher.group();
                if (hex.charAt(0) == '#')
                    hex = hex.substring(1);

                if (hex.length() != 6) {
                    context.getTypedMessaging().replyWarning("Hex code has to be 6 digits. (" + hex.length() + ")");
                    return;
                }

            } else {
                context.getTypedMessaging().replyWarning("Could not recognise colour from the given value");
                return;
            }
            color = Color.decode('#' + hex);
        }
        String RGBvalues = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
        int unsignedInt = Integer.parseUnsignedInt(hex, 16);
        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, sender.getUser());
        builder.setTitle("Values of #" + hex);
        builder.setColor(color);
        builder.addField("RGB", RGBvalues, true); // RGB Values
        builder.addField("Decimal", String.valueOf(unsignedInt), true); // Decimal Value
        builder.addField("Binary", Integer.toBinaryString(unsignedInt), true); // Binary Value
        context.reply(builder.build());

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
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "Value", "Name/RGB/Hex", ArgumentType.REQUIRED));
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
