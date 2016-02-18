package martacus.mart.bot.rpg.fightsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import martacus.mart.bot.Main;

public class PlayerStats {
	
	public static double getHealth(String userId) throws SQLException{
		String sql = "SELECT health FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double health = 0;
		while(res.next()){
			health = res.getDouble("health");
		}
		return health;
	}
	
	public static double getStrength(String userId) throws SQLException{
		String sql = "SELECT strength FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double strength = 0;
		while(res.next()){
			strength = res.getDouble("strength");
		}
		return strength;
	}
	
	public static double getIntelligence(String userId) throws SQLException{
		String sql = "SELECT intelligence FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double intelligence = 0;
		while(res.next()){
			intelligence = res.getDouble("intelligence");
		}
		return intelligence;
	}
	
	public static double getDexterity(String userId) throws SQLException{
		String sql = "SELECT dexterity FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double dexterity = 0;
		while(res.next()){
			dexterity = res.getDouble("dexterity");
		}
		return dexterity;
	}
	
	public static double getBlockChance(String userId) throws SQLException{
		String sql = "SELECT blockchance FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double blockchance = 0;
		while(res.next()){
			blockchance = res.getDouble("blockchance");
		}
		return blockchance;
	}
	
	public static double getLuck(String userId) throws SQLException{
		String sql = "SELECT luck FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userId);
		ResultSet res = state.executeQuery();
		double luck = 0;
		while(res.next()){
			luck = res.getDouble("luck");
		}
		return luck;
	}

}
