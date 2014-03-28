package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsonparser.Movie;
import jsonparser.Parser;
import wordplay.Tagger;
import classifier.Classifier;
import database.Database;
import evaluation.Evaluation;
/**
 * The implementation of the ISystem interface. This class is used to control then
 * entire model area of the system; it performs the connection between the database
 * component and the parsing engine, the classification process, the thesaurus
 * construction component and finally the evaluation aspect.
 * 
 * @author Fiona MacIsaac
 *
 */
public class ProjectSystem implements ISystem {

	private Database db = null;
	private BufferedReader br = null;
	private FileReader reader = null;
	private Parser parser = null;
	private Tagger tagger = null;
	private Classifier cls = null;
	private Evaluation eval = null;

	public ProjectSystem() {

		/*
		 * Instantiate the database class with the url to connection to
		 * the database.
		 */
		db = new Database("jdbc:mysql://localhost:3306/fdb11130");
		parser = new Parser();
		cls = new Classifier();
		tagger = new Tagger();
	}


	/**
	 * Passes the variable through to the database class to
	 * create the film table.
	 * 
	 * @param name
	 */
	public void createFilmList(String name) {
		db.dbCreateFilmList(name);
	}

	/**
	 * Pass the name variable through to the database class to
	 * create the linker table between genres and films.
	 * 
	 * @param name
	 */
	public void createFGLink(String name) {
		db.dbCreateFGLink(name);
	}

	/**
	 * Take a file of JSON represented films, parse these into Movie
	 * objects and add these Movie objects to the specified film list
	 * table and linker table within the database.
	 * 
	 * @param filepath
	 * @param list
	 * @param fg
	 */
	public void addMovieList(String filepath, String list, String fg) {
		Movie movie = null;
		try {
			movie = new Movie();
			reader = new FileReader(filepath);
			br = new BufferedReader(reader);
			String line = "";

			while ((line = br.readLine()) != null) {

				movie = parser.parseMovie(line);
				if (movie != null) {
					db.dbInsertMovie(movie, list, fg);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Take a file of JSON represented genres and parse these into
	 * a map which is then added to the database.
	 * 
	 * @param filepath
	 */
	public void addGenreList(String filepath) {
		Map<Long, String> genreMap = null;
		try {
			genreMap = new HashMap<Long, String>();
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

	/**
	 * Call the database method to create a training set table
	 * using the user provided suffix.
	 * 
	 * @param suffix
	 */
	public void createTrainingTable(String suffix) {
		db.dbCreateTrainingSet(suffix);
	}

	/**
	 * Call the database method to create a test set table
	 * using the user provided suffix.
	 * 
	 * @param suffix
	 */
	public void createTestTable(String suffix) {
		db.dbCreateTestSet(suffix);
	}

	/**
	 * Create a training set of the given size and populate this data into
	 * the table with the given suffix.
	 * Once the training data has been selected, use the remaining content of
	 * the source table to populate the test set.
	 * 
	 * @param size
	 * @param suffix
	 * @param source
	 */
	public void createTrainingSet(int size, String suffix, String source) {
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> movies = null;
		List<Integer> training = null;
		for (Integer genreid : genres) {
			int i = 0;
			movies = new ArrayList<Integer>(db.dbGetMoviesForGenreList(genreid,
					source));
			training = new ArrayList<Integer>();
			int listSize = movies.size();
			while ((i < size) && (i < listSize)) {
				training.add(movies.remove(0));
				i++;
			}
			db.dbPopulateTrainingSet(training, genreid, suffix);
			db.dbPopulateTestSet(movies, genreid, suffix);
		}
	}

	/**
	 * Faciliatate the creation of the thesaurus table
	 * from the user input.
	 * 
	 * @param name
	 */
	public void createThesaurus(String name) {
		db.dbCreateThesaurus(name);
	}

	/**
	 * Faciliate the creation of a custom stop word list based off
	 * the content of the filepath.
	 * 
	 * @param filepath
	 */
	public void setStopWords(String filepath) {
		tagger.createStopList(filepath);
	}

	/**
	 * Populate the thesaurus using the original algorithm. Do this by
	 * pulling the data from the training set defined by the suffix on a per
	 * genre basis. During this process the user defined filter is selected
	 * within the system for use by the algorithm.
	 * 
	 * @param filter
	 * @param name
	 * @param suffix
	 */
	public void populateThesaurus(String filter, String name, String suffix) {
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> films = null;
		String overview = "";
		for (Integer genreid : genres) {
			films = db.dbGetMoviesForGenreList(genreid, "TrainingSet" + suffix);
			for (Integer filmid : films) {
				overview = db.dbGetOverview(filmid);
				tagger.setFilter(overview, filter);
				tagger.applyFilter();
			}
			db.dbPopulateThesaurus(tagger.getWords(), genreid, name);
			tagger.clearWords();
		}
	}

	/**
	 * Populate the thesaurus using the second algorithm. Do this by
	 * pulling the data from the training set defined by the suffix on a per
	 * genre basis. During this process the user defined filter is selected
	 * within the system for use by the algorithm. The complementary filter methods
	 * are used for this second algorithm.
	 * 
	 * @param filter
	 * @param name
	 * @param suffix
	 */
	public void populateThesaurus2(String filter, String name, String suffix) {
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> films = null;
		String overview = "";
		for (Integer genreid : genres) {
			films = db.dbGetMoviesForGenreList(genreid, "TrainingSet" + suffix);
			for (Integer filmid : films) {
				overview = db.dbGetOverview(filmid);
				tagger.setFilter2(overview, filter);
				tagger.applyFilter2();
			}
			db.dbPopulateThesaurus(tagger.getWords(), genreid, name);
			tagger.clearWords();
		}
	}

	/**
	 * Facilitate training of the classifier with respect to the original
	 * thesaurus construction algorithm.
	 * 
	 * This involves adding the thesaurus to the dataset for the classifier
	 * on a per genre basis, training the algorithm then preserving the
	 * knowledge base for use during classification.
	 * 
	 * @param name
	 */
	public void trainClassifier(String name) {
		Map<Integer, String> genreList = db.dbGetGenreListMap();

		for (Integer genreid : genreList.keySet()) {
			List<String> wordsList = db.dbGetThesaurus(genreid, name);
			cls.addToDataset(genreList.get(genreid), cls.readLines(wordsList));
		}
		cls.trainClassifier();
		cls.setKnowledgeBase();
		cls.resetClassifier();
	}

	/**
	 * Classify the data within the test set by taking the overview for
	 * each film in the test set and use the classifier to predict the genre.
	 * 
	 * This data is then added to a map to be added to the database once classification
	 * is completed.
	 * 
	 * @param suffix
	 */
	public void classifyTestData(String suffix) {
		cls.prepClassifier();
		Map<Integer, Integer> testSet = db.dbGetTestSet(suffix);
		String overview = "";
		String genre = "";
		String temp = "";
		for (Integer e : testSet.keySet()) {
			overview = db.dbGetOverview(e);
			if (tagger.getStemFilterStatus()) {
				temp = tagger.classifyStemFilter(overview);
			} else {
				temp = overview;
			}
			genre = cls.classifyData(temp);
			cls.setClassified(e, db.dbGetGenreID(genre));
		}

	}

	/**
	 * Facilitate training of the classifier with respect to the original
	 * thesaurus construction algorithm.
	 * 
	 * This involves adding the words with frequencies between the given thresholds
	 * to the dataset for the classifier on a per genre basis, training the algorithm
	 * then preserving the knowledge base for use during classification.
	 * 
	 * @param name
	 * @param upper
	 * @param lower
	 */
	public void trainClassifier2(String name, int upper, int lower) {
		Map<Integer, String> genreList = db.dbGetGenreListMap();

		for (Integer genreid : genreList.keySet()) {
			List<String> wordsList = db.dbApplyThreshold(name, genreid, upper,
					lower);
			cls.addToDataset(genreList.get(genreid), cls.readLines(wordsList));
		}
		cls.trainClassifier();
		cls.setKnowledgeBase();
		cls.resetClassifier();
	}

	/**
	 * Facilitate the creation of the table to hold the classified data.
	 * 
	 * @param name
	 */
	public void createClassified(String name) {
		db.dbCreateClassifiedTable(name);
	}

	/**
	 * Add the classified data to the specific table in the database.
	 * 
	 * @param name
	 */
	public void archiveClassified(String name) {
		db.dbPopulateClassified(name, cls.getClassifiedData());
	}

	/**
	 * Create a table to hold the overall evaluation data for each
	 * classification experiment.
	 */
	public void createEval() {
		db.dbCreateEvalTable("Total_Eval");
	}

	/**
	 * Facilitate creation of an evaluation table for a classification
	 * experiment.
	 * 
	 * @param name
	 */
	public void createEvalGenre(String name) {
		db.dbCreateEvalGenreTable(name);
	}

	/**
	 * Run evaluation on a classification experiment. Do this on a per genre basis.
	 * Add the resulting scores per genre to the specified table.
	 * @param table
	 * @param test
	 * @param classified
	 */
	public void runEvalPerGenre(String table, String test, String classified) {
		Map<Integer, Integer> testMap = null;
		Map<Integer, Integer> classifiedMap = null;
		List<Integer> genres = db.dbGetGenreList();
		double precision = 0;
		double recall = 0;
		double fmeasure = 0;
		int genre_count = 0;
		for (int genreid : genres) {
			eval = new Evaluation();
			testMap = db.dbGetMoviesForGenreMap(genreid, "TestSet" + test);
			classifiedMap = db.dbGetMoviesForGenreMap(genreid, classified);

			genre_count++;
			eval.runEvaluation(testMap, classifiedMap);

			db.dbAddEvalGenre(table, genreid, eval.getPrecision(),
					eval.getRecall(), eval.getFmeasure());

			if (eval.getPrecision() > 0 && eval.getRecall() > 0
					&& eval.getFmeasure() > 0) {
				precision += eval.getPrecision();
				recall += eval.getRecall();
				fmeasure += eval.getFmeasure();
			}
		}

		precision = (precision / genre_count);
		recall = (recall / genre_count);
		fmeasure = (fmeasure / genre_count);

		db.dbAddEval("Total_Eval", classified, precision, recall, fmeasure);

	}

	/**
	 * Check if a table exists within the database.
	 * 
	 * @param name
	 */
	public boolean tableExists(String name) {
		return db.dbTableExists(name);
	}

	/**
	 * Return the list of tables within the database.
	 */
	public List<String> getTables() {
		return db.dbListTables();
	}

	/**
	 * Delete the content of the table specified.
	 * 
	 * @param name
	 */
	public void deleteContent(String name) {
		db.dbDeleteFromTable(name);
	}

}
