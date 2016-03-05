package martacus.mart.bot.rpg.fightsystem;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import martacus.mart.bot.Main;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

public class FightingHandler {
	
	List<Monster> monsters = new ArrayList<Monster>();
	Random rand = new Random();
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userId = m.getAuthor().getID();
		if(m.getContent().startsWith("[fight")){
			if(messagesplit.length != 2){
				sendMessage("Invalid usage, use [fight [level]", event);
				return;
			}
			else{
				if(!isFightOngoing(userId)){
					if(Integer.parseInt(messagesplit[1]) >= 1){
						int level = Integer.parseInt(messagesplit[1]);
						Monster mons = new Monster(level, userId);
						monsters.add(mons);
						newFight(mons, userId, event);
					}
					else{
						sendMessage("This would only get you negative xp, and items will be lvl 1. You sure?", event);
					}
				}
				else{
					sendMessage("You are already fighting!", event);
				}
			}
		}
		if(m.getContent().startsWith("[attack")){
			if(isFightOngoing(userId)){
				m.delete();
				attackMonster(userId, event);
			}
			else{
				sendMessage("No fight going bluh succer", event);
			}
		}
	}
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sender = event.getMessage().getAuthor().toString();
		new MessageBuilder(Main.pub).appendContent(sender+"\n").appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}
	
	public void sendAttackMessage(MessageReceivedEvent event, double playerdamage, double playerhp, double monsterdamage, double monsterhp) throws 
	HTTP429Exception, DiscordException, MissingPermissionsException{
		String sender = event.getMessage().getAuthor().toString();
		new MessageBuilder(Main.pub).appendContent(sender+"\n").appendContent("``Damage done: " + playerdamage + " | Monster HP: " + monsterhp)
		.appendContent(" | Damage Taken: " + monsterdamage + " | Player HP: " + playerhp + "``")
		.withChannel(event.getMessage().getChannel()).build();
	}
	
	public double roundDouble(double number){
		DecimalFormat df = new DecimalFormat("#.#"); df.setRoundingMode(RoundingMode.CEILING); //Optional
		number = Double.parseDouble(df.format(number));
		return number;
	}
	
	
	void newFight(Monster mon, String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sql = "INSERT INTO fights(playerID) VALUES(?)";
		try {
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userId);
			state.executeUpdate();
			sendMessage("Fight started! Attack!", event);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean isFightOngoing(String userId){
		String sql = "SELECT playerID FROM fights";
		try {
			PreparedStatement state = Main.conn.prepareStatement(sql);
			ResultSet rslt = state.executeQuery();
			while(rslt.next()){
				String id = rslt.getString("playerID");
				if(id.equals(userId)){
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	void attackMonster(String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, SQLException{
		double damage = getPlayerDamage(userId, event);
		for(Monster s : monsters){
			if(s.getOpponent().equals(userId)){
				double hp = s.getHealth() - damage;
				hp = roundDouble(hp);
				s.setHealth(hp);
				checkMonsterHealth(userId, event);
				attackPlayer(userId, event, damage, hp);
			}
		}
	}
	
	void attackPlayer(String userId, MessageReceivedEvent event, double playerDamage, double monsterHp) throws SQLException, HTTP429Exception, DiscordException, MissingPermissionsException{
		double damage = getMonsterDamage(userId);
		double randomValue = 0.9 + (1.1 - 0.9) * rand.nextDouble();
		damage *= randomValue;
		setPlayerHealth(userId, damage, event, playerDamage, monsterHp);
	}
	
	void setPlayerHealth(String userId, double damage, MessageReceivedEvent event, double playerDamage, double monsterHp) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sql = "SELECT health FROM playerstats WHERE playerID=?";
		String sql2 = "UPDATE playerstats SET health=? WHERE playerID=?";
		PreparedStatement state;
		try {
			state = Main.conn.prepareStatement(sql);
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state.setString(1, userId);
			state2.setString(2, userId);
			ResultSet res = state.executeQuery();
			while(res.next()){
				double health = res.getDouble("health");
				damage = roundDouble(damage);
				health -= damage;
				health = roundDouble(health);
				state2.setDouble(1, health);
				state2.executeUpdate();
				sendAttackMessage(event, playerDamage, health, damage, monsterHp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	double getPlayerDamage(String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sql = "SELECT weaponID FROM body WHERE playerID=?";
		String sql2 = "SELECT Rating FROM items WHERE ID=?";
		String sql3 = "SELECT strength FROM playerstats WHERE playerID=?";
		try {
			PreparedStatement state = Main.conn.prepareStatement(sql);
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			PreparedStatement state3 = Main.conn.prepareStatement(sql3);
			state.setString(1, userId);
			state3.setString(1, userId);
			ResultSet results = state.executeQuery();
			ResultSet results3 = state3.executeQuery();
			if (!results.next() ) {
				sendMessage("You dont have an item equipped! You give him a punch!", event);
				return 1;
			}
			results.absolute(0);
			while(results.next()){
				int id = results.getInt("weaponID");
				state2.setInt(1, id);
				ResultSet results2 = state2.executeQuery();
				while(results2.next()){
					double damage = results2.getDouble("Rating");
					while(results3.next()){
						double strength = results3.getDouble("strength");
						double randomValue = 0.9 + (1.1 - 0.9) * rand.nextDouble();
						damage *= randomValue;
						damage *= strength;
						damage = roundDouble(damage);
						return damage;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1.1;
	}
	
	double getMonsterDamage(String userId){
		for(Monster s : monsters){
			if(s.getOpponent().equals(userId)){
				double damage = s.getDamage();
				return damage;
			}
		}
		return 1.2;
	}
	
	void checkMonsterHealth(String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		for(Monster s : monsters){
			if(s.getOpponent().equals(userId)){
				if(s.getHealth() <= 0){
					double xp = s.getLevel() * 10;
					sendMessage("You defeated the monster! Here is your reward: " + xp + "xp", event);
					monsters.remove(s);
					try {
						addExp(userId, xp);
						String sql = "DELETE FROM fights WHERE playerID=?";
						PreparedStatement state = Main.conn.prepareStatement(sql);
						state.setString(1, userId);
						state.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void addExp(String userId, double xp) throws SQLException{
		String sql = "UPDATE players SET Exp=Exp + ? WHERE ID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setDouble(1, xp);
		state.setString(2, userId);
		state.executeUpdate();
		
	}
}
