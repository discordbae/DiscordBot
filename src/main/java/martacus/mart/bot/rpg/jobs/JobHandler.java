package martacus.mart.bot.rpg.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import martacus.mart.bot.Main;
import martacus.mart.bot.rpg.SQLGet;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

@SuppressWarnings("unused")
public class JobHandler {
	
	Timer timer = new Timer();
	List<String> onthejob = new ArrayList<String>();


	@EventSubscriber
	public void OnMesageEvent(MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException, IOException, 
	 SQLException {
		IMessage m = event.getMessage();
		String message = m.toString();
		String[] messagesplit = message.split(" ");
		String userID = m.getAuthor().getID();
		IPrivateChannel channel = event.getClient().getOrCreatePMChannel(event.getMessage().getAuthor());
		if(message.startsWith("[takejob")){
			if(checkIfInJob(userID)){
				sendMessage("You are already doing a job! Wait until your current job is over.", event);
				return;
			}
			determineJob(messagesplit[1], userID, channel, event);
		}
	}
	
	void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		String sender = event.getMessage().getAuthor().toString();
		new MessageBuilder(Main.pub).appendContent(sender+"\n").appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}

	void sendPrivateMessage(String message, IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		channel.sendMessage(message);
	}
	
	void determineJob(String job, final String userID, final IPrivateChannel channel, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		if(job.equalsIgnoreCase("woodcutting")){
			Job jobb = new Job("Woodcutting", 5);
			newJob(jobb, userID, channel);
			sendMessage("You started the job! It will take 5 minutes to finish, until then you can't do much.", event);
		}
		if(job.equalsIgnoreCase("mining")){
			Job jobb = new Job("Mining", 5);
			newJob(jobb, userID, channel);
			sendMessage("You started the job! It will take 5 minutes to finish, until then you can't do much.", event);
		}
		if(job.equalsIgnoreCase("farming")){
			Job jobb = new Job("Farming", 7);
			newJob(jobb, userID, channel);
			sendMessage("You started the job! It will take 5 minutes to finish, until then you can't do much.", event);
		}
		if(job.equalsIgnoreCase("scavenging")){
			Job jobb = new Job("Scavenging", 2);
			newJob(jobb, userID, channel);
			sendMessage("You started the job! It will take 5 minutes to finish, until then you can't do much.", event);
		}
	}
	
	void newJob(final Job job, final String userID, final IPrivateChannel channel){
		onthejob.add(userID);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() { 
		   @Override  
		   public void run() {
		       onthejob.remove(userID); 
		       try {
				executeJobEnd(userID, job, channel);
			} catch (MissingPermissionsException | HTTP429Exception
					| DiscordException e) {
				e.printStackTrace();
			}
		   }
		},  300 * 1000);
	}
	
	int calculatePayment(int basePay, String userID){
		int money;
		money = basePay * 5;
		return money;
	}
	
	boolean checkIfInJob(String userID){
		for(String s : onthejob){
			if(s.equals(userID)){
				return true;
			}
		}
		return false;
	}
	
	void executeJobEnd(String userID, Job job, final IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		if(job.getJobType().equals("Woodcutting")){
			executeWoodcutting(userID, job, channel);
		}
		if(job.getJobType().equals("Mining")){
			executeMining(userID, job, channel);
		}
		if(job.getJobType().equals("Farming")){
			executeFarming(userID, job, channel);
		}
		if(job.getJobType().equals("Scavenging")){
			executeScavenging(userID, job, channel);
		}
	}
	
	
	void executeWoodcutting(String userID, Job job, final IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		final int monPerMin = job.getMoneyPerMin();
 	    int money = calculatePayment(monPerMin, userID);
 	    sendPrivateMessage("The job is done! You earned: " + money + " gold.", channel);
 	    SQLGet.addMoney(userID, money);
 	    double chanceNumber = Main.rand.nextInt(100) +1;
 	    double chanceCommon = 50, chanceUnCommon = 30, chanceRare = 15, chanceEpic = 5;
 	    if(chanceNumber < chanceCommon){
 	    	sendPrivateMessage("You found something back in the woods. The item has been added to your inventory.", channel);
 	    }
	}
	
	void executeMining(String userID, Job job, final IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		final int monPerMin = job.getMoneyPerMin();
 	    int money = calculatePayment(monPerMin, userID);
 	    sendPrivateMessage("The job is done! You earned: " + money + " gold.", channel);
 	    SQLGet.addMoney(userID, money);
 	    double chanceNumber = Main.rand.nextInt(100) +1;
 	    double chanceCommon = 50, chanceUnCommon = 30, chanceRare = 15, chanceEpic = 5;
 	    if(chanceNumber < chanceCommon){
 	    	sendPrivateMessage("You found something back in the mines. The item has been added to your inventory.", channel);
 	    }
	}
	
	void executeFarming(String userID, Job job, final IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		final int monPerMin = job.getMoneyPerMin();
 	    int money = calculatePayment(monPerMin, userID);
 	    sendPrivateMessage("The job is done! You earned: " + money + " gold.", channel);
 	    SQLGet.addMoney(userID, money);
 	    double chanceNumber = Main.rand.nextInt(100) +1;
 	    double chanceRare = 15, chanceEpic = 5;
 	    if(chanceNumber < chanceRare){
 	    	sendPrivateMessage("You found something back in the fields. Its very rare to find something here!"
 	    			+ " The item has been added to your inventory.", channel);
 	    }
	}
	
	void executeScavenging(String userID, Job job, final IPrivateChannel channel) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		//Not even finished/started
	}

}
