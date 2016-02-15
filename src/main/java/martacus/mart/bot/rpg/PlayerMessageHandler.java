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
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

@SuppressWarnings("unused")
public class PlayerMessageHandler  {
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userId = m.getAuthor().getID();
		IUser user = m.getAuthor();
		if(m.getContent().startsWith("[rpg")){
			if(messagesplit.length == 1){
				try {
					IPrivateChannel channel = Main.pub.getOrCreatePMChannel(user);
					new MessageBuilder(Main.pub).withChannel(channel)
					.withContent("Commands", MessageBuilder.Styles.UNDERLINE_BOLD_ITALICS).appendContent("\n")
					.appendContent("[rpg - For the rpg commands", MessageBuilder.Styles.ITALICS).appendContent("\n")
					.appendContent("[rpg join - Joins the game", MessageBuilder.Styles.ITALICS).appendContent("\n").build();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
		if(m.getContent().startsWith("[join")){
			Player player = new Player(userId, 1, 0.0);
			newPlayer(player, userId, event, user);
		}
		if(m.getContent().startsWith("[char")){
			getCharacter(userId, user, event);
		}
		if(m.getContent().startsWith("[setclass")){
			if(messagesplit.length != 2){
				sendMessage("Invalid sub commands. Use: [setclass [class]", event);
				return;
			}
			else{
				setClass(userId, event, messagesplit);
			}
			
		}
	 }
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(Main.pub).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}
	
	void newPlayer(Player p, String userId, MessageReceivedEvent event, IUser user) throws SQLException, HTTP429Exception, 
	DiscordException, MissingPermissionsException{
		final String id = p.getID();
		final int level = p.getLevel();
		final double exp = p.getXp();
		ResultSet results = null;
		//See if player exists, if yes DIE
		try{
			Statement state = Main.conn.createStatement();
			results = state.executeQuery("SELECT ID FROM players WHERE ID='"+ userId +"'");
		} catch(SQLException e) { e.printStackTrace();}
		while(results.next()){
			if(results.getString("ID") != null){
				sendMessage("You have already started an adventure!", event);
				return;
			}
		}
		//Add player
		try {
			Statement state = Main.conn.createStatement();
			state.executeUpdate("INSERT INTO players(ID, level, exp) VALUES('" + id + "', " + level + ", " + exp + ")");
			state.executeUpdate("INSERT INTO body(playerID) VALUES('" + id + "')");
			sendMessage("Welcome adventurer!", event);
			state.close();
		} catch(SQLException e) { e.printStackTrace();}
	}

	void getCharacter(String userId, IUser user, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, 
	MissingPermissionsException{
		ResultSet results = null;
		int level = 0;
		double xp = 0;
		String classs = null;
		try {
			Statement state = Main.conn.createStatement();
			results = state.executeQuery("SELECT Level, Exp, Class FROM players WHERE ID='"+ userId +"'");	
			while(results.next()){
				level = results.getInt("Level");
				xp = results.getDouble("Exp");
				classs = results.getString("Class");
			}
			new MessageBuilder(Main.pub).appendContent(user + "\n").appendContent("Level: " + level + "\nXp: " + xp + "\nClass: " + classs + "")
			.withChannel(event.getMessage().getChannel()).build();
			state.close();
		} catch (SQLException e) {e.printStackTrace();}
		
	}
	
	
	void setClass(String userId, MessageReceivedEvent event, String[] array) throws SQLException, HTTP429Exception, DiscordException, 
	MissingPermissionsException{
		Statement state = Main.conn.createStatement();
		ResultSet results = null;
		results = state.executeQuery("SELECT Class FROM players WHERE ID='"+ userId +"'");
		if (results.next()) {
		    String s = results.getString("Class");
		    if (results.wasNull()) {}else{sendMessage("You have already chosen a class!", event);return;}
		}
		if(array[2].equals("Warrior")){
			state.executeUpdate("UPDATE players SET Class='"+ array[2] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[2], userId);
		}
		else if(array[2].equals("Mage")){
			state.executeUpdate("UPDATE players SET Class='"+ array[2] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[2], userId);
		}
		else if(array[2].equals("Ranger")){
			state.executeUpdate("UPDATE players SET Class='"+ array[2] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[2], userId);
		}
		else if(array[2].equals("DemonicAngel")){
			if(userId.equals("131414719666847745")){
				state.executeUpdate("UPDATE players SET Class='"+ array[2] +"' WHERE ID='" + userId +"'");
				sendMessage("Class Set!", event); giveClassItem(array[2], userId);
			}
			else{
				sendMessage("You aint Xei, the righteous heir to this title!", event);
			}
		}
		else{
			sendMessage("Class does not exist!", event);
		}
		state.close();
	}
	
	void giveClassItem(String classs, String userId) throws SQLException{
		Statement state = Main.conn.createStatement();
		if(classs.equals("Warrior")){
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 1)");
		}
		if(classs.equals("Mage")){
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 2)");
		}
		if(classs.equals("Ranger")){
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 3)");
		}
		if(classs.equals("DemonicAngel")){
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 1)");
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 2)");
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', 3)");
		}
		state.close();
	}
	
	
}
