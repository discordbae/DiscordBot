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
		return 0;
	}
	
	//Fights Table
	public static void addFight(String userID){
		String sql = "INSERT INTO fights(playerID) VALUES(?)";
		try { 
			PreparedStatement state = Main.conn.prepareStatement(sql);
			state.setString(1, userID);
			state.executeUpdate();
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Items Table
	public static double getItemRating(int itemID){
		try {
			String sql2 = "SELECT Rating FROM items WHERE ID=?";
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state2.setInt(1, itemID);
			ResultSet results2 = state2.executeQuery();
			while(results2.next()){
				double rating = results2.getDouble("Rating");
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
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state2.setInt(1, itemID);
			ResultSet resultname = state2.executeQuery();
			while(resultname.next()){
				String name = resultname.getString("name");
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
			PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state2.setString(1, itemName);
			ResultSet results2 = state2.executeQuery();
			while(results2.next()){
				int id = results2.getInt("ID");
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//Inventory Table
	public static List<Integer> getInventoryItemsID(String userID) throws SQLException{
		List<Integer> ints = new ArrayList<Integer>();
		String sql = "SELECT itemID FROM inventory WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setString(1, userID);
		ResultSet results = state.executeQuery();
		while(results.next()){
			int result = results.getInt("itemID");
			ints.add(result);
		}
		return ints;
	}
	
	//Player stats Table
	public static double getPlayerStat(String userID, String attribute) throws SQLException{
		String sql = "";
		switch(attribute){
			case "level": sql = "SELECT level FROM playerstats WHERE playerID=?";
						  PreparedStatement state = Main.conn.prepareStatement(sql);
						    state.setString(1, userID);
							ResultSet rslt = state.executeQuery();
							while(rslt.next()){
								int att = rslt.getInt(1);
								return att;
							}
							break;
			case "health":  sql = "SELECT health FROM playerstats WHERE playerID=?";
							break;
			case "exp":  sql = "SELECT exp FROM playerstats WHERE playerID=?";
							break;
			case "strength":  sql = "SELECT strength FROM playerstats WHERE playerID=?";
							break;
			case "intelligence":  sql = "SELECT intelligence FROM playerstats WHERE playerID=?";
							break;
			case "dexterity":  sql = "SELECT dexterity FROM playerstats WHERE playerID=?";
							break;
			case "blockchance":  sql = "SELECT blockchance FROM playerstats WHERE playerID=?";
							break;
			case "luck":  sql = "SELECT luck FROM playerstats WHERE playerID=?";
							break;
			default: break;
			}
		PreparedStatement state = Main.conn.prepareStatement(sql);
	    state.setString(1, userID);
		ResultSet rslt= state.executeQuery();
		while(rslt.next()){
			double att = rslt.getDouble(1);
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addExp(String userId, double xp) throws SQLException{
		String sql = "UPDATE playerstats SET exp=exp + ? WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		state.setDouble(1, xp);
		state.setString(2, userId);
		state.executeUpdate();
	}

}
