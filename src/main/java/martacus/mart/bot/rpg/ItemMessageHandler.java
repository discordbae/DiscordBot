package martacus.mart.bot.rpg;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import martacus.mart.bot.Main;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

@SuppressWarnings("unused")
public class ItemMessageHandler {
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userId = m.getAuthor().getID();
		IUser user = m.getAuthor();
		if(m.getContent().startsWith("[rpg")){
			if(messagesplit[1].equals("additem")){
				if(messagesplit.length != 5){
					sendMessage("Not an valid item. Please specify all the traits.", event);
				}
				else{
					Item item=new Item(messagesplit[2], messagesplit[3], Double.parseDouble(messagesplit[4]));
					createItem(item, userId, event);
				}
			}
		}
	 }
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(Main.pub).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}
	
	public void createItem(Item i, String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, 
	MissingPermissionsException{
		final String name = i.getName();
		final String type = i.getType();
		final double rating = i.getRating();
		
		try {
			Statement state = Main.conn.createStatement();
			state.executeUpdate("INSERT INTO items(Name, ItemType, Rating) VALUES('" +name+ "', '" +type+ "', " +rating+ ")");
			sendMessage("Item added!", event);
			state.close();
		} catch(SQLException e) { e.printStackTrace();}
	}

}
