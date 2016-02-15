package martacus.mart.bot.rpg;

public class Monster {

	private int level;
	private double health;
	private double damage;
	
	Monster(int lvl){
		setLevel(lvl);
		setHealth(lvl*20);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}
}
