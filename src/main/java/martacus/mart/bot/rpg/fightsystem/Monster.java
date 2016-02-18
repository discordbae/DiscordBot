package martacus.mart.bot.rpg.fightsystem;

public class Monster {

	private int level;
	private double health;
	private double damage;
	private double armor;
	private String opponent;
	
	Monster(int lvl, String oppon){
		setLevel(lvl);
		setHealth(lvl * 20);
		setDamage(lvl * 5);
		setOpponent(oppon);
		setArmor(lvl*1);
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

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public double getArmor() {
		return armor;
	}

	public void setArmor(double armor) {
		this.armor = armor;
	}
}
