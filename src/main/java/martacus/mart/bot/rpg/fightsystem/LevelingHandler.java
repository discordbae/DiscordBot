package martacus.mart.bot.rpg.fightsystem;

import java.sql.SQLException;

import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import martacus.mart.bot.Main;
import martacus.mart.bot.rpg.SQLGet;


public class LevelingHandler {
	
	public static void checkLevelUp(String userID, MessageReceivedEvent event) throws SQLException, HTTP429Exception, DiscordException, MissingPermissionsException{
		double level = SQLGet.getPlayerStat(userID, "level");
		double xpNeed = experienceForLevel(level + 1);
		double xp = SQLGet.getPlayerStat(userID, "exp");
		if(xp >= xpNeed){
			SQLGet.addPlayerLevel(userID);
			sendMessage("You went level up! your level is: " + (level + 1), event);
		}
	}
	
	public static void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sender = event.getMessage().getAuthor().toString();
		new MessageBuilder(Main.pub).appendContent(sender+"\n").appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}
	
	public static double experienceForLevel(double level)
	{
		double total = 0;
		for (int i = 1; i < level; i++)
		{
			total += Math.floor(i + 200 * Math.pow(2, i / 8.0));
		}

		return Math.floor(total / 4);
	}

}
