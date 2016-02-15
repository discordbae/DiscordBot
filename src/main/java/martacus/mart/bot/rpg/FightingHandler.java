package martacus.mart.bot.rpg;

import java.io.IOException;
import java.sql.SQLException;

import martacus.mart.bot.Main;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

public class FightingHandler {
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userId = m.getAuthor().getID();
		IUser user = m.getAuthor();
		if(m.getContent().startsWith("[fight")){
			if(messagesplit.length != 3){
				sendMessage("Invalid usage, use [fight [level] [players]", event);
			}
		}
	}
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(Main.pub).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}

}
