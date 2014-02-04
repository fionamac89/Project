package main;

import java.sql.SQLException;

import database.Database;

public class Run {

	public static void main(String[] args) {
		Database db = new Database(
				"/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt");
		db.run();
		/*try {
			db.dbDeleteMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		
		/*Database dbg = new Database(
				"/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");
		dbg.runGenre();*/

	}

}
