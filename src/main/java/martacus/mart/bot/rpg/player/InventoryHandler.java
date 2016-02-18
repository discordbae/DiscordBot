package martacus.mart.bot.rpg.player;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import martacus.mart.bot.Main;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

public class InventoryHandler {
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userId = m.getAuthor().getID();
		if(m.getContent().startsWith("[inventory")){
			showInventory(userId, event);
		}
		if(m.getContent().startsWith("[equip")){
			if(messagesplit.length != 2){
				sendMessage("Invalid sub commands. Use: [equip [item]", event);
				return;
			}
			else{
				equipItem(messagesplit[1], userId, event);
			}
		}
	}
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(Main.pub).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}
	
	void showInventory(String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, 
	MissingPermissionsException, SQLException{
		String sql = "SELECT itemID FROM inventory WHERE playerID=?";
		String sql2 = "SELECT Name FROM items WHERE ID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		PreparedStatement state2 = Main.conn.prepareStatement(sql2);
			state.setString(1, userId);
			ResultSet results = state.executeQuery();
			ResultSet resultname = null;
			String invent = "";
			while(results.next()){
				int result = results.getInt("itemID");
				state2.setInt(1, result);
				resultname = state2.executeQuery();
				while(resultname.next()){
					String name = resultname.getString("Name");
					invent += (name + "\n");
				}
			}
			sendMessage(invent, event);		
	}
	
	void equipItem(String item, String userId, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, SQLException{
		String sql = "SELECT ID FROM items WHERE Name=?";
		String sql2 = "SELECT itemID FROM inventory WHERE playerID=?";
		String sql3 = "SELECT weaponID FROM body WHERE playerID=?";
		PreparedStatement state = Main.conn.prepareStatement(sql);
		PreparedStatement state2 = Main.conn.prepareStatement(sql2);
		PreparedStatement state3 = Main.conn.prepareStatement(sql3);
		state.setString(1, item);
		state2.setString(1, userId);
		state3.setString(1, userId);
		ResultSet results = state.executeQuery();
		ResultSet invitems = state2.executeQuery();
		ResultSet equipeditem = state3.executeQuery();
		if (!results.next() ) {
			sendMessage("This item doesnt exist!", event);
			return;
		}
		results.absolute(0);
		while(results.next()){
			int id = results.getInt("ID");
			while(invitems.next()){
				int id2 = invitems.getInt("itemID");
				if(id == id2){
					addItemToBody(userId, event, id2);
					//state.close(); state2.close(); results.close(); invitems.close();
					equipeditem.absolute(0);
					deleteItemFromInv(userId, event, id2);
					while(equipeditem.next()){
						int id3 = equipeditem.getInt("weaponID");
						addItemToInv(userId, event, id3);
					}
					return;
				}
			}
			sendMessage("You dont have the item!",event);
		
		}	
	}
	
	void deleteItemFromInv(String userId, MessageReceivedEvent event, int id){
		Statement state;
		try {
			state = Main.conn.createStatement();
			state.executeUpdate("DELETE FROM inventory WHERE playerID='"+userId+"' AND itemID="+id+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addItemToInv(String userId, MessageReceivedEvent event, int id){
		Statement state;
		ResultSet result;
		try {
			state = Main.conn.createStatement();
			result = state.executeQuery("SELECT ItemType FROM items WHERE ID=" + id +"");
			while(result.next()){
				String type = result.getString("ItemType");
				if(type.equals("Head")){
					
				}
			}
			state.executeUpdate("INSERT INTO inventory(playerID, itemID) VALUES('"+userId+"', "+id+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addItemToBody(String userId, MessageReceivedEvent event, int id) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sql = "SELECT ItemType FROM items WHERE ID=?";
		PreparedStatement state;
		Statement state2;
		try {
			state = Main.conn.prepareStatement(sql);
			state2 = Main.conn.createStatement();
			state.setInt(1, id);
			ResultSet result = state.executeQuery();
			while(result.next()){
				String type = result.getString("ItemType");
				if(type.equals("Head")){
					state2.executeUpdate("UPDATE body SET headID="+id+" WHERE playerID='"+userId+"'");
				}
				if(type.equals("Chest")){
					state2.executeUpdate("UPDATE body SET chestID="+id+" WHERE playerID='"+userId+"'");
				}
				if(type.equals("Pants")){
					state2.executeUpdate("UPDATE body SET pantsID="+id+" WHERE playerID='"+userId+"'");
				}
				if(type.equals("Boots")){
					state2.executeUpdate("UPDATE body SET bootsID="+id+" WHERE playerID='"+userId+"'");
				}
				if(type.equals("Melee")||type.equals("Magic")||type.equals("Ranged")){
					state2.executeUpdate("UPDATE body SET weaponID="+id+" WHERE playerID='"+userId+"'");
				}
				sendMessage("Item equipped!",event);
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
