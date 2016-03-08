package martacus.mart.bot.rpg.fightsystem;

import java.sql.SQLException;

import martacus.mart.bot.rpg.SQLGet;


public class LevelingHandler {
	
	public static void checkLevelUp(String userId) throws SQLException{
		double level = SQLGet.getPlayerStat(userId, "level");
	}
	
	
	public static double experienceForLevel(int level)
	{
		double total = 0;
		for (int i = 1; i < level; i++)
		{
			total += Math.floor(i + 200 * Math.pow(2, i / 8.0));
		}

		return Math.floor(total / 4);
	}

}
