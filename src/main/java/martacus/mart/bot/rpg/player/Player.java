package martacus.mart.bot.rpg.player;


public class Player {
	
	private String ID;
	private int level;
	private double xp;
	
	Player(String id, int lvl, double exp){
		setID(id);
		setLevel(lvl);
		setXp(exp);
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getXp() {
		return xp;
	}

	public void setXp(double xp) {
		this.xp = xp;
	}

}
