package martacus.mart.bot;

import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

public class MessageHandler {
	
	StrArrays str = new StrArrays();
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException {
		IMessage m = event.getMessage();
		String message = m.toString();
		if(m.getContent().startsWith("[boom")){
			sendMessage("http://www.theblaze.com/wp-content/uploads/2013/08/explosion.jpg", event);
		}
		if(m.getContent().startsWith("[hi")){
			sendMessage("Here, have some attention! :D", event);
		}
		if(m.getContent().startsWith("[dog")){
			sendMessage("Here, have some attention! :D", event);
		}
		if(m.getContent().startsWith("[sleep")){
			String[] messagesplit = message.split(" ");
			int i = str.randomNumber(str.sleep);
			sendMessage(messagesplit[1] + str.sleep[i], event);
		}
		if(m.getContent().startsWith("[mal")){
			String[] messagesplit = message.split(" ");
			sendMessage("http://myanimelist.net/animelist/" + messagesplit[1], event);
		}
		if(m.getContent().startsWith("[hb")){
			String[] messagesplit = message.split(" ");
			sendMessage("https://hummingbird.me/users/" + messagesplit[1] + "/library", event);
		}
		if(m.getContent().startsWith("[dwi")){
			int i = str.randomNumber(str.dwi);
			sendMessage(str.dwi[i], event);
		}
		if(m.getContent().startsWith("[oppai")){
			sendMessage("They are filled with men's hopes and dreams.", event);
		}
		if(m.getContent().startsWith("[duel")){
			String[] messagesplit = message.split(" ");
			int i = str.rand(2);
			String sender = m.getAuthor().toString();
			sendMessage("I challange you to a duel, " + messagesplit[1] + "!", event);
			if(i == 1){
				sendMessage(sender +" strikes a mighty blow on " + messagesplit[1] + ", taking them down in 1 hit! " + sender + " wins!", event);
			}
			else{
				sendMessage(messagesplit[1] + " strikes " + sender + " deep in his heart! Resulting in a win for " + messagesplit[1] + "!", event);
			}
		}
		if (m.getContent().startsWith("[meme") || m.getContent().startsWith("[nicememe")) {
				new MessageBuilder(Main.pub).appendContent("MEMES REQUESTED:", MessageBuilder.Styles.UNDERLINE_BOLD_ITALICS)
						.appendContent(" http://niceme.me/").withChannel(event.getMessage().getChannel())
						.build();}
	 }
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(Main.pub).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}

}
