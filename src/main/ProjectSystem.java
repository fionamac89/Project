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

import jsonparser.Movie;
import jsonparser.Parser;
import wordplay.Tagger;
import classifier.Classifier;
import database.Database;

public class ProjectSystem implements ISystem {

	private Database db = null;
	private BufferedReader br = null;
	private FileReader reader = null;
	private Parser parser = null;
	private Movie movie = null;
	private Tagger tagger = null;
	private Map<Long, String> genreMap = null; 
	private Classifier cls = null;

	public ProjectSystem() {
		db = new Database();
		parser = new Parser();
		movie = new Movie();
		genreMap = new HashMap<Long, String>();
		cls = new Classifier();
	}

	public void addMovie() {

	}

	public void addMovieList(String filepath) {
		try {
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
		for (Integer genreid : genres) {
			int i = 0;
			movies = db.dbGetMoviesForGenre(genreid);
			int listSize = movies.size();
			while ((i < size) && (i < listSize)) {
				training.add(movies.remove(0));
				i++;
			}
			System.out.println(genreid + ": " + training.size());
			db.dbPopulateTrainingSet(training, genreid);
			createTestSet(movies, genreid);
			training.clear();
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

	// Alter this mechanism to do everything by Genre. May need new query to get
	// all films from training set with GenreID

	public void populateThesaurus() {
		tagger = new Tagger();
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> films = null;
		String overview = "";
		for (Integer genreid : genres) {
			films = db.dbGetMoviesForGenreTrainSet(genreid);
			for (Integer filmid : films) {
				overview = db.dbGetOverview(filmid);
				tagger.removeStopWords(overview);
			}
			db.dbPopulateThesaurus(tagger.getWords(), genreid);
			System.out.println(genreid + ": " + tagger.getWords());
			tagger.clearWords();
		}
	}

	public void trainClassifier() {
		Map<Integer, String> genreList = db.dbGetGenreListMap();
		
		for(Integer genreid : genreList.keySet()) {
			List<String> wordsList = db.dbGetThesaurus(genreid);
			cls.addToDataset(genreList.get(genreid), cls.readLines(wordsList));
		}
		cls.trainClassifier();
		cls.setKnowledgeBase();
		cls.resetClassifier();
	}

	public void classifyTestData() {
		cls.prepClassifier();
		Map<Integer, Integer> testSet = db.dbGetTestSet();
		String overview = "";
		String genre = "";
		for(Integer e : testSet.keySet()) {
			overview = db.dbGetOverview(e);
			genre = cls.classifyData(overview);
			cls.setClassified(e, db.dbGetGenreID(genre));
		}
		
	}
	
	public void archiveClassified() {
		db.dbPopulateClassified(cls.getClassifiedData());
	}


}
