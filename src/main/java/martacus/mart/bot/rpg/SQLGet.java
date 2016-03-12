package martacus.mart.bot.rpg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import martacus.mart.bot.Main;

public class SQLGet {
	
	//Body Table
	public static int getBodyItemID(String userID, String itemType) throws SQLException{
		String sql = "SELECT "+itemType+" FROM body WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userID);
		ResultSet rslt = state.executeQuery();
		while(rslt.next()){
			int att = rslt.getInt(1);
			return att;
		}
		state.close();
		return 0;
	}
	
	//Fights Table
	public static void addFight(String userID){
		String sql = "INSERT INTO fights(playerID) VALUES(?)";
		try { 
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFightOngoing(String userId){
		String sql = "SELECT playerID FROM fights";
		try {
			PreparedStatement state = Main.conn.prepareStatement(sql);
			ResultSet rslt = state.executeQuery();
			while(rslt.next()){
				String id = rslt.getString("playerID");
				if(id.equals(userId)){
					state.close();
					rslt.close();
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void delFight(String userID){
		try {
			
			String sql = "DELETE FROM fights WHERE playerID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Items Table
	public static double getItemRating(int itemID){
		try {
			String sql = "SELECT Rating FROM items WHERE ID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setInt(1, itemID);
			ResultSet results = state.executeQuery();
			while(results.next()){
				double rating = results.getDouble("Rating");
				state.close();
				results.close();
				return rating;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	
	public static String getItemName(int itemID){
		try {
			String sql2 = "SELECT name FROM items WHERE ID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql2);
			state.setInt(1, itemID);
			ResultSet resultname = state.executeQuery();
			while(resultname.next()){
				String name = resultname.getString("name");
				state.close();
				resultname.close();
				return name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "oke";
	}
	
	public static int getItemsItemID(String itemName){
		try {
			String sql2 = "SELECT ID FROM items WHERE name=?";
			PreparedStatement state = Main.conn.prepareStatement(sql2);
			state.setString(1, itemName);
			ResultSet results = state.executeQuery();
			while(results.next()){
				int id = results.getInt("ID");
				state.close();
				results.close();
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//Inventory Table
	public static List<Integer> getInventoryItemsID(String userID){
		List<Integer> ints = new ArrayList<Integer>();
		String sql = "SELECT itemID FROM inventory WHERE playerID=?";
		PreparedStatement state;
		try {
			state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			ResultSet results = state.executeQuery();
			while(results.next()){
				int result = results.getInt("itemID");
				ints.add(result);
			}
			state.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ints;
	}
	
	//Player stats Table
	public static double getPlayerStat(String userID, String attribute) throws SQLException{
		String sql = "";
		if(attribute.equals("level")){
			sql = "SELECT level FROM playerstats WHERE playerID=?";
			  PreparedStatement state = Main.conn.prepareStatement(sql);
			    state.setString(1, userID);
				ResultSet rslt = state.executeQuery();
				while(rslt.next()){
					int att = rslt.getInt(1);
					state.close();
					rslt.close();
					return att;
				}
		}
		if(attribute.equals("health")){
			sql = "SELECT health FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("exp")){
			sql = "SELECT exp FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("strength")){
			sql = "SELECT strength FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("intelligence")){
			sql = "SELECT intelligence FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("dexterity")){
			sql = "SELECT dexterity FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("blockchance")){
			sql = "SELECT blockchance FROM playerstats WHERE playerID=?";
		}
		if(attribute.equals("luck")){
			sql = "SELECT luck FROM playerstats WHERE playerID=?";
		}

		PreparedStatement state = Main.conn.prepareStatement(sql);
	    state.setString(1, userID);
		ResultSet rslt= state.executeQuery();
		while(rslt.next()){
			double att = rslt.getDouble(1);
			state.close();
			rslt.close();
			return att;
		}

		return 101.0;
	}
	
	public static String GetClass(String userID) throws SQLException{
		String sql = "SELECT class FROM playerstats WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userID);
		ResultSet rslt = state.executeQuery();
		while(rslt.next()){
			String att = rslt.getString(1);
			state.close();
			rslt.close();
			return att;
		}
		return "0";
	}
	
	public static void setPlayerHealth(double health, String userID){
		String sql2 = "UPDATE playerstats SET health=? WHERE playerID=?";
		try {
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state2.setString(2, userID);
			state2.setDouble(1, health);
			state2.executeUpdate();
			state2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addExp(String userID, double xp) throws SQLException{
		String sql = "UPDATE playerstats SET exp=exp + ? WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setDouble(1, xp);
		state.setString(2, userID);
		state.executeUpdate();
		state.close();
	}

	public static void addPlayerLevel(String userID){
		try {
			String sql = "UPDATE playerstats SET level=level + 1 WHERE playerID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			state.executeUpdate();
			addStatPoints(userID);
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void upgradePlayerStats(String userID, String stat, int points){
		try {
			String sql = "";
			if(stat.equals("strength")){
				sql = "UPDATE playerstats SET strength=strength + ? WHERE playerID=?";
			}
			if(stat.equals("dexterity")){
				sql = "UPDATE playerstats SET dexterity=dexterity + ? WHERE playerID=?";
			}
			if(stat.equals("intelligence")){
				sql = "UPDATE playerstats SET intelligence=intelligence + ? WHERE playerID=?";
			}
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setInt(1, points);
			state.setString(2, userID);
			state.executeUpdate();
			state.close();
			decreaseStatPoints(userID, points);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//currency
	public static int getStatpoints(String userID){
		try {
			String sql = "SELECT statpoints FROM currency WHERE playerID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			ResultSet resultname = state.executeQuery();
			while(resultname.next()){
				int points = resultname.getInt("statpoints");
				state.close(); resultname.close();
				return points;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void addStatPoints(String userID){
		try {
			String sql = "UPDATE currency SET statpoints=statpoints + 3 WHERE playerID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void decreaseStatPoints(String userID, int points){
		try {
			String sql = "UPDATE currency SET statpoints=statpoints - ? WHERE playerID=?";
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setInt(1, points);
			state.setString(2, userID);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
