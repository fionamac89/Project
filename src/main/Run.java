package main;

import database.Database;

public class Run {

	public static void main(String[] args) {
		Database db = new Database(
				"/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/testInput.txt");
		db.run();
		
		/*Database dbg = new Database(
				"/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");
		dbg.runGenre();*/

	}

}
