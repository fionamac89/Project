package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jsonparser.Movie;
import jsonparser.Parser;
import database.Database;

public class System implements ISystem {

	private Database db = null;
	private BufferedReader br = null;
	private FileReader reader = null;
	private Parser parser = null;
	private Movie movie = null;
	private Map<Long, String> genreMap = null;

	public System() {
		db = new Database();
		parser = new Parser();
		movie = new Movie();
		genreMap = new HashMap<Long, String>();
	}

	public void addMovie() {

	}

	public void addMovieList(String filepath) {
		try {
			db.dbConnect();
			reader = new FileReader(filepath);
			br = new BufferedReader(reader);
			String line = "";

			while ((line = br.readLine()) != null) {

				movie = parser.parseMovie(line);
				if (movie != null) {
					db.dbInsertMovie(movie);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addGenre() {

	}

	public void addGenreList(String filepath) {
		try {
			db.dbConnect();
			reader = new FileReader(filepath);
			br = new BufferedReader(reader);
			String line = br.readLine();
			
			genreMap = parser.parseGenre(line);
			db.dbInsertGenre(genreMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createTrainingSet(int size) {
		
	}

	public void createTestSet() {

	}

	public void createThesaurus(String name) {
		
	}

	public void populateThesaurus() {
		
	}

	public void classifyTestData() {

	}

	public void viewClassifiedData() {

	}

}
