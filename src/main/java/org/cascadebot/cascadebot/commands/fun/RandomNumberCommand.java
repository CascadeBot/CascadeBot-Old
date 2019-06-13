/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import java.io.IOException;
import java.util.Random;

public class RandomNumberCommand implements ICommandMain {

	@Override
	public void onCommand(Member sender, CommandContext context) {
	    Random randomObj = new Random();
		try {
		    String argRaw = context.getArg(0);   
		    if (argRaw.matches("[0-9]+") == false) {	
		        context.getTypedMessaging().replyInfo("Please provide numbers only");
		    }
		    
		    else {
		        int argOne = Integer.parseInt(argRaw);
		        int randomNumberInt = randomObj.nextInt(argOne);
		        String randomNumber = Integer.toString(randomNumberInt);
		        context.getTypedMessaging().replyInfo("Random number is " + randomNumber);
		    }
		    
		} catch(Exception ArrayIndexOutOfBoundsException) {
		      context.getTypedMessaging().replyInfo("No arguments given");
	      }
	}
	      
	@Override
	public String command() {
        return "randnum";
	}

	@Override
	public Module getModule() {
	    return Module.FUN;
	}

	@Override
	public CascadePermission getPermission() {
	    return CascadePermission.of("Random number command", "randnum", true);
	}

	@Override
	public String description() {
	    return "Returns a random number";
	}

}
