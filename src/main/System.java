package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wordplay.Tagger;
import jsonparser.Movie;
import jsonparser.Parser;
import database.Database;

public class System implements ISystem {

	private Database db = null;
	private BufferedReader br = null;
	private FileReader reader = null;
	private Parser parser = null;
	private Movie movie = null;
	private Tagger tagger = null;
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
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> movies = null;
		List<Integer> training = new ArrayList<Integer>();
		int i = 0;
		for (Integer genreid : genres) {
			movies = db.dbGetMoviesForGenre(genreid);
			while((i < size) && (i < movies.size())){
				training.add(movies.remove(0));
				i++;
			}
			db.dbPopulateTrainingSet(training, genreid);
			createTestSet(movies, genreid);
		}
	}

	public void createTestSet(List<Integer> movies, int genreid) {
		List<Integer> test = new ArrayList<Integer>(movies);
		db.dbPopulateTestSet(test, genreid);
	}

	/*
	 * Not needed?
	 */
	public void createThesaurus(String name) {
		
	}

	public void populateThesaurus(List<Integer> training, int genreid) {
		tagger = new Tagger();
		Map<Integer, String> overviews = db.dbGetOverview(training);
		for (Entry<Integer, String> e : overviews.entrySet()) {
			tagger.removeStopWords(e.getValue());
		}
		
		
	}

	public void classifyTestData() {

	}

	public void viewClassifiedData() {

	}

}
