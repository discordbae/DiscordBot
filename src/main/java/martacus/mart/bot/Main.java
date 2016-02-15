package martacus.mart.bot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import martacus.mart.bot.rpg.FightingHandler;
import martacus.mart.bot.rpg.InventoryHandler;
import martacus.mart.bot.rpg.ItemMessageHandler;
import martacus.mart.bot.rpg.PlayerMessageHandler;
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
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws DiscordException, NetworkException, OAuthException, SQLException, IOException{
		Properties obj = new Properties();
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

		IDiscordClient client = new ClientBuilder().withLogin(email, password).build();
		client.login();
		pub = client;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new MessageHandler());
		dispatcher.registerListener(new PlayerMessageHandler());
		dispatcher.registerListener(new ItemMessageHandler());
		dispatcher.registerListener(new InventoryHandler());
		dispatcher.registerListener(new FightingHandler());

	    connect();
	}
	
	public static void connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://localhost/discordrpg?user=root&password=");
		Statement state = conn.createStatement();
		state.executeUpdate("CREATE TABLE IF NOT EXISTS players(ID varchar(255), level int, exp double, PRIMARY KEY(ID))");	
		state.executeUpdate("CREATE TABLE IF NOT EXISTS inventory(playerID varchar(255), itemID int(11) , "
				+ "FOREIGN KEY(playerID) references players(ID), FOREIGN KEY(itemID) references items(ID))");	
		state.executeUpdate("CREATE TABLE IF NOT EXISTS body(ID int(11) AUTO_INCREMENT,playerID varchar(255), headID int(11) ,chestID int(11) ,pantsID int(11) ,"
				+ "bootsID int(11), weaponID int(11) , FOREIGN KEY(playerID) references players(ID), FOREIGN KEY(headID) references items(ID)"
				+ ", FOREIGN KEY(chestID) references items(ID), FOREIGN KEY(pantsID) references items(ID)"
				+ ", FOREIGN KEY(bootsID) references items(ID), PRIMARY KEY(ID), FOREIGN KEY(weaponID) references items(ID))");	
	}
	

}
