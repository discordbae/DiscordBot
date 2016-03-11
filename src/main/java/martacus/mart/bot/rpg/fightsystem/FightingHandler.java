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
		double playerDamage = getPlayerDealtDamage(userId, event);
		for(Monster s : monsters){
			if(s.getOpponent().equals(userId)){
				double hp = s.getHealth() - playerDamage;
				hp = roundDouble(hp);
				s.setHealth(hp);
				checkMonsterHealth(userId, event);
				attackPlayer(userId, event, playerDamage, hp);
			}
		}
	}
	
	void attackPlayer(String userId, MessageReceivedEvent event, double playerDamage, double monsterHp) throws SQLException, HTTP429Exception, DiscordException, MissingPermissionsException{
		double monsterDamage = getMonsterDamage(userId);
		double randomValue = 0.9 + (1.1 - 0.9) * rand.nextDouble();
		monsterDamage *= randomValue;
		setPlayerHealth(userId, monsterDamage, event, playerDamage, monsterHp);
	}
	
	void setPlayerHealth(String userID, double monsterDamage, MessageReceivedEvent event, double playerDamage, double monsterHp) throws SQLException, HTTP429Exception, DiscordException, MissingPermissionsException{
		double playerHealth = SQLGet.getPlayerStat(userID, "health");
		monsterDamage = roundDouble(monsterDamage);
		playerHealth -= monsterDamage;
		playerHealth = roundDouble(playerHealth);
		SQLGet.setPlayerHealth(playerHealth, userID);
		sendAttackMessage(event, playerDamage, playerHealth, monsterDamage, monsterHp);
	}
	
	double getPlayerDealtDamage(String userID, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, SQLException{
		int id = SQLGet.getBodyItemID(userID, "weaponID");
		if (id == 0) {
			sendMessage("You dont have an item equipped! You give him a punch!", event);
		}
		double damage = SQLGet.getItemRating(id);
		double playerValue = getPlayerDamageStat(userID);
		double randomValue = 0.9 + (1.1 - 0.9) * rand.nextDouble();
		damage *= randomValue;
		damage *= playerValue;
		damage = roundDouble(damage);
		return damage;
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
					LevelingHandler.checkLevelUp(userID, event);
				}
			}
		}
	}
	
	double getPlayerDamageStat(String userID) throws SQLException{
		String playerClass = SQLGet.GetClass(userID);
		if(playerClass.equals("Ranger")){
			double dex = SQLGet.getPlayerStat(userID, "dexterity");
			return dex;
		}
		if(playerClass.equals("Mage")){
			double inte = SQLGet.getPlayerStat(userID, "intelligence");
			return inte;
		}
		if(playerClass.equals("Warrior")){
			double str = SQLGet.getPlayerStat(userID, "strength");
			return str;
		}
		if(playerClass.equals("DemonicAngel")){
			double str = SQLGet.getPlayerStat(userID, "strength");
			return str;
		}
		return 0;
	}
}
