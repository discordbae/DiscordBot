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
import martacus.mart.bot.rpg.SQLGet;
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
				if(!SQLGet.isFightOngoing(userId)){
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
			if(SQLGet.isFightOngoing(userId)){
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
	
	
	void newFight(Monster mon, String userID, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		SQLGet.addFight(userID);
		sendMessage("Fight started! Attack!", event);
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
	
	void setPlayerHealth(String userID, double damage, MessageReceivedEvent event, double playerDamage, double monsterHp) throws SQLException, HTTP429Exception, DiscordException, MissingPermissionsException{
		double health = SQLGet.getPlayerStat(userID, "health");
		damage = roundDouble(damage);
		health -= damage;
		health = roundDouble(health);
		SQLGet.setPlayerHealth(health, userID);
		sendAttackMessage(event, playerDamage, health, damage, monsterHp);
	}
	
	double getPlayerDamage(String userID, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sql = "SELECT weaponID FROM body WHERE playerID=?";
		try {
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			ResultSet results = state.executeQuery();
			if (!results.next() ) {
				sendMessage("You dont have an item equipped! You give him a punch!", event);
				return 1;
			}
			results.absolute(0);
			while(results.next()){
				int id = results.getInt("weaponID");
				double damage = SQLGet.getItemRating(id);
				double strength = SQLGet.getPlayerStat(userID, "strength");
				double randomValue = 0.9 + (1.1 - 0.9) * rand.nextDouble();
				damage *= randomValue;
				damage *= strength;
				damage = roundDouble(damage);
				return damage;
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
	
	void checkMonsterHealth(String userID, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, SQLException{
		for(Monster s : monsters){
			if(s.getOpponent().equals(userID)){
				if(s.getHealth() <= 0){
					double xp = s.getLevel() * 10;
					sendMessage("You defeated the monster! Here is your reward: " + xp + "xp", event);
					monsters.remove(s);
					SQLGet.addExp(userID, xp);
					SQLGet.delFight(userID);
				}
			}
		}
	}
}
