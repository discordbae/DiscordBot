package martacus.mart.bot.rpg.player;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import martacus.mart.bot.Main;
import martacus.mart.bot.rpg.fightsystem.PlayerStats;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageBuilder.Styles;

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
					Styles ubi = MessageBuilder.Styles.UNDERLINE_BOLD_ITALICS;
					Styles i = MessageBuilder.Styles.ITALICS;
					IPrivateChannel channel = Main.pub.getOrCreatePMChannel(user);
					new MessageBuilder(Main.pub).withChannel(channel)
					.withContent("Commands", ubi).appendContent("\n")
					.appendContent("[rpg - For the rpg commands",i).appendContent("\n")
					.appendContent("[join - Joins the game",i).appendContent("\n")
					.appendContent("[setclass [class] - Set the desired classes: Warrior, Ranger, Mage",i).appendContent("\n")
					.appendContent("[stats -  Check out your current stats",i).appendContent("\n")
					.appendContent("[char - Shows info about your character",i).appendContent("\n")
					.appendContent("[inventory - Shows the items in your inventory",i).appendContent("\n")
					.appendContent("[equip [item name] - Equips the item",i).appendContent("\n")
					.appendContent("[fight [monster level] - Fight a monster of the desired level",i).appendContent("\n")
					.appendContent("[attack  - Attacks the monster",i).build();
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
		if(m.getContent().startsWith("[stats")){
			double hp = PlayerStats.getHealth(userId);
			double str = PlayerStats.getStrength(userId);
			double inte = PlayerStats.getIntelligence(userId);
			double dex = PlayerStats.getDexterity(userId);
			double block = PlayerStats.getBlockChance(userId);
			double luck = PlayerStats.getLuck(userId);
			sendMessage("Health: " + hp + "\nStrength: " + str + "\nIntelligence " + inte + "\nDexterity " + dex + "\nBlock Chance: " + block 
						+ "\nLuck: " + luck , event);
		}
	 }
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sender = event.getMessage().getAuthor().toString();
		new MessageBuilder(Main.pub).appendContent(sender+"\n").appendContent(message).withChannel(event.getMessage().getChannel()).build();
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
			state.executeUpdate("INSERT INTO playerstats(playerID, health, strength, intelligence, dexterity, blockchance, luck) "
								+ "VALUES('" + id + "',100,1,1,1,1,1)");
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
		if(array[1].equals("Warrior")){
			state.executeUpdate("UPDATE players SET Class='"+ array[1] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("Mage")){
			state.executeUpdate("UPDATE players SET Class='"+ array[1] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("Ranger")){
			state.executeUpdate("UPDATE players SET Class='"+ array[1] +"' WHERE ID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("DemonicAngel")){
			if(userId.equals("131414719666847745")){
				state.executeUpdate("UPDATE players SET Class='"+ array[1] +"' WHERE ID='" + userId +"'");
				sendMessage("Class Set!", event); giveClassItem(array[1], userId);
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
