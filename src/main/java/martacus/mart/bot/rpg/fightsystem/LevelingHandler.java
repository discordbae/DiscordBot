package martacus.mart.bot.rpg.fightsystem;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

public class LevelingHandler {
	
	public void checkLevelUp(String userId, MessageReceivedEvent event){
		
	}
	
	public static void calcXpNeeded(int level){
		int levels = 100;
	    int xp_for_first_level = 50;
	    long experience = (long) ((float)xp_for_first_level*(Math.pow(1.2, level)));
	    experience = Math.round(experience);
	    System.out.println(experience);
	}

}
