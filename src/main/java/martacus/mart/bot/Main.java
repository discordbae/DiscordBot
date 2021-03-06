package martacus.mart.bot;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

import martacus.mart.bot.rpg.fightsystem.FightingHandler;
import martacus.mart.bot.rpg.jobs.JobHandler;
import martacus.mart.bot.rpg.player.InventoryHandler;
import martacus.mart.bot.rpg.player.ItemMessageHandler;
import martacus.mart.bot.rpg.player.PlayerMessageHandler;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.OAuthException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.EventDispatcher;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@SuppressWarnings("unused")
public class Main {
	
	private static String email, password;
	public static IDiscordClient pub;
	public static Connection conn = null;
	static UserAgent myUserAgent = UserAgent.of("desktop", "martacus.mart.bot", "v0.1", "Martacus");
	
	public static Random rand = new Random();
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws DiscordException, NetworkException, OAuthException, SQLException, IOException{
	    Scanner in = new Scanner(System.in);
	    String email = in.next();
	    String password = in.next();
	    in.close();
	    if(email.equalsIgnoreCase("mart")){
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader("C:\\Development\\Discord\\Spartacus\\details.txt"));
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("email")) {
					email = reader.nextString();
				} else if (name.equals("pass")) {
					password = reader.nextString();
				}
			}
	    }


		IDiscordClient client = new ClientBuilder().withLogin(email, password).build();
		client.login();
		pub = client;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new MessageHandler());
		dispatcher.registerListener(new PlayerMessageHandler());
		dispatcher.registerListener(new ItemMessageHandler());
		dispatcher.registerListener(new InventoryHandler());
		dispatcher.registerListener(new FightingHandler());
		dispatcher.registerListener(new JobHandler());

	    connect();
	    
	}
	
	public static void connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://localhost/discordrpg?user=root&password=");
		Statement state = conn.createStatement();
		//Player table
		state.executeUpdate("CREATE TABLE IF NOT EXISTS players(ID varchar(255), PRIMARY KEY(ID))");	
		//All items
		state.executeUpdate("CREATE TABLE IF NOT EXISTS items(ID int(11) AUTO_INCREMENT,name varchar(255),itemtype varchar(255),"
				+ "rating int(11), PRIMARY KEY(ID))");	 
		//Inventory table
		state.executeUpdate("CREATE TABLE IF NOT EXISTS inventory(ID int(11) AUTO_INCREMENT, playerID varchar(255), itemID int(11) , "
							+ "FOREIGN KEY(playerID) references players(ID), FOREIGN KEY(itemID) references items(ID), PRIMARY KEY(ID))");	
		//Body table (Players equiped items)
		state.executeUpdate("CREATE TABLE IF NOT EXISTS body(ID int(11) AUTO_INCREMENT,playerID varchar(255), headID int(11) ,"
							+ "chestID int(11) ,pantsID int(11) , bootsID int(11), weaponID int(11) , "
							+ "FOREIGN KEY(playerID) references players(ID), FOREIGN KEY(headID) references items(ID)"
							+ ", FOREIGN KEY(chestID) references items(ID), FOREIGN KEY(pantsID) references items(ID)"
							+ ", FOREIGN KEY(bootsID) references items(ID), PRIMARY KEY(ID), FOREIGN KEY(weaponID) references items(ID))");	
		//Ongoing table for Pve fights.
		state.executeUpdate("CREATE TABLE IF NOT EXISTS fights(ID int(11) AUTO_INCREMENT,playerID varchar(255), PRIMARY KEY(ID))");	

		//Player stats table
		state.executeUpdate("CREATE TABLE IF NOT EXISTS playerstats(playerID varchar(255), health DOUBLE PRECISION, level int(11), "
						   + "exp DOUBLE PRECISION, strength DOUBLE PRECISION, "
				           + "intelligence DOUBLE PRECISION, dexterity DOUBLE PRECISION, blockchance DOUBLE PRECISION, "
				           + "luck DOUBLE PRECISION, class varchar(255), "
				           + "PRIMARY KEY(playerID), FOREIGN KEY(playerID) references players(ID))");
		//Currency table
		state.executeUpdate("CREATE TABLE IF NOT EXISTS currency(playerID varchar(255),money int(11),statpoints int(11), PRIMARY KEY(playerID))");
		//Skills table
		state.executeUpdate("CREATE TABLE IF NOT EXISTS skills(playerID varchar(255), woodcutting int(11), mining int(11), "
				+ "farming int(11), scavenging int(11), PRIMARY KEY(playerID))");
	}
	
}
