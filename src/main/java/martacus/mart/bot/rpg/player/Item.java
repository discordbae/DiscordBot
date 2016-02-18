package martacus.mart.bot.rpg.player;

public class Item {

	private String name;
	private String type;
	private double rating;
	
	Item(String namee, String typee, double ratingg){
		setName(namee);
		setType(typee);
		setRating(ratingg);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
}
