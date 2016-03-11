package martacus.mart.bot.rpg.player;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import martacus.mart.bot.Main;
import martacus.mart.bot.rpg.SQLGet;
import martacus.mart.bot.rpg.fightsystem.LevelingHandler;
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
					.appendContent("[character - Shows info about your character",i).appendContent("\n")
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
			newPlayer(userId, event, user);
		}
		if(m.getContent().startsWith("[character")){
			getCharacter(userId, user, event);
		}
		if(m.getContent().startsWith("[upstats")){
			if(messagesplit.length != 3){
				sendMessage("Invalid sub commands. Use: [upstats [stats] [amount of points]", event);
				return;
			}
			else if(SQLGet.getStatpoints(userId) <= Integer.parseInt(messagesplit[2])){
				sendMessage("You do not have enough stat points to do this!" + SQLGet.getStatpoints(userId), event);
				return;
			}
			else if(messagesplit[1].equals("strength") || messagesplit[1].equals("dexterity") || messagesplit[1].equals("intelligence")){
				SQLGet.upgradePlayerStats(userId, messagesplit[1], Integer.parseInt(messagesplit[2]));
				sendMessage(messagesplit[2] + " points have been added to " + messagesplit[1], event);
			}
			else{
				sendMessage("Stat doesnt exist!", event);
			}
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
	
	void newPlayer(String userId, MessageReceivedEvent event, IUser user) throws SQLException, HTTP429Exception, 
	DiscordException, MissingPermissionsException{
		ResultSet results = null;
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
			state.executeUpdate("INSERT INTO players(ID) VALUES('" + userId + "')");
			state.executeUpdate("INSERT INTO body(playerID) VALUES('" + userId + "')");
			state.executeUpdate("INSERT INTO playerstats(playerID, health, level, exp, strength, intelligence, dexterity, blockchance, luck) "
								+ "VALUES('" + userId + "',100,1,0,1,1,1,1,1)");
			state.executeUpdate("INSERT INTO currency(playerID, money, statpoints) VALUES('" + userId + "',0,0)");
			sendMessage("Welcome adventurer!", event);
			state.close();
		} catch(SQLException e) { e.printStackTrace();}
	}

	void getCharacter(String userID, IUser user, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, 
	MissingPermissionsException, SQLException{
		ResultSet results = null;
		double level = SQLGet.getPlayerStat(userID, "level");
		double exp = SQLGet.getPlayerStat(userID, "exp");
		double health = SQLGet.getPlayerStat(userID, "health");
		double strength = SQLGet.getPlayerStat(userID, "strength");
		double intelligence = SQLGet.getPlayerStat(userID, "intelligence");
		double dexterity = SQLGet.getPlayerStat(userID, "dexterity");
		double blockchance = SQLGet.getPlayerStat(userID, "blockchance");
		double luck = SQLGet.getPlayerStat(userID, "luck");
		String classs = SQLGet.GetClass(userID);
			new MessageBuilder(Main.pub).appendContent(user + "		" + classs + "\n")
			.appendContent("Level: " + level + "\t\t\tXp: " + exp)
			.appendContent("\nStr: " + strength + "\t\t\tBlockchance: " + blockchance)
			.appendContent("\nInt: " + intelligence + "\t\t\tLuck: " + luck)
			.appendContent("\nDex: " + dexterity)
			.withChannel(event.getMessage().getChannel()).build();
		
	}
	
	
	void setClass(String userId, MessageReceivedEvent event, String[] array) throws SQLException, HTTP429Exception, DiscordException, 
	MissingPermissionsException{
		Statement state = Main.conn.createStatement();
		ResultSet results = null;
		results = state.executeQuery("SELECT class FROM playerstats WHERE playerID='"+ userId +"'");
		if (results.next()) {
		    String s = results.getString("Class");
		    if (results.wasNull()) {}else{sendMessage("You have already chosen a class!", event);return;}
		}
		if(array[1].equals("Warrior")){
			state.executeUpdate("UPDATE playerstats SET class='"+ array[1] +"' WHERE playerID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("Mage")){
			state.executeUpdate("UPDATE playerstats SET class='"+ array[1] +"' WHERE playerID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("Ranger")){
			state.executeUpdate("UPDATE playerstats SET class='"+ array[1] +"' WHERE playerID='" + userId +"'");
			sendMessage("Class Set!", event); giveClassItem(array[1], userId);
		}
		else if(array[1].equals("DemonicAngel")){
			if(userId.equals("131414719666847745")){
				state.executeUpdate("UPDATE playerstats SET class='"+ array[1] +"' WHERE playerID='" + userId +"'");
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
	
	void giveClassItem(String classs, String userId){
		Statement state;
		try {
			state = Main.conn.createStatement();
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
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
}
