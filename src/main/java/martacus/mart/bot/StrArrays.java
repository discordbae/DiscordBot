package martacus.mart.bot;

import java.util.Random;

public class StrArrays {
	
	Random rand = new Random(); 
	
	String[] sleep = {" you need your beauty sleep, dont you?", " you look sleepy m8, you better go right about now!",
			          " what about you go to bed, its getting late isn't it?", ", SleepiusTonightos! Dont mind it, its bad i know.."};
	String[] dwi = {"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-02.gif",
					"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-03.gif",
					"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-04.gif",
					"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-05.gif",
					"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-06.gif",
					"http://memesvault.com/wp-content/uploads/Deal-With-It-Gif-09.gif",
					"https://media.giphy.com/media/GODSCQebffJzW/giphy.gif"};

	public int randomNumber(String[] array){
		int value = rand.nextInt(array.length); 
		return value;
	}
	
	public int rand(int valuee){
		int value = rand.nextInt(valuee);
		return value;
	}
}
