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

public class ProjectSystem implements ISystem {

	private Database db = null;
	private BufferedReader br = null;
	private FileReader reader = null;
	private Parser parser = null;
	private Tagger tagger = null;
	private Classifier cls = null;
	private Evaluation eval = null;

	public ProjectSystem() {
		db = new Database("jdbc:mysql://localhost:3306/fdb11130");
		parser = new Parser();
		cls = new Classifier();
		tagger = new Tagger();
	}

	public void testDbConnect() {
		db.dbConnect();
	}

	public void createFilmList(String name) {
		db.dbCreateFilmList(name);
		System.out.println("Film list created");
	}

	public void createFGLink(String name) {
		db.dbCreateFGLink(name);
		System.out.println("FGLink created");
	}

	public void addMovie() {

	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addGenre() {

	}

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

	public void createTrainingTable(String suffix) {
		db.dbCreateTrainingSet(suffix);
		System.out.println("Training Table created");
	}

	public void createTestTable(String suffix) {
		db.dbCreateTestSet(suffix);
		System.out.println("Test Table created");
	}

	public void createTrainingSet(int size, String suffix, String source) {
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> movies = null;
		List<Integer> training = null;
		for (Integer genreid : genres) {
			int i = 0;
			movies = new ArrayList<Integer>(db.dbGetMoviesForGenreList(genreid,
					source));
			System.out.println("Movies: " + movies);
			training = new ArrayList<Integer>();
			int listSize = movies.size();
			while ((i < size) && (i < listSize)) {
				training.add(movies.remove(0));
				i++;
			}
			System.out.println(genreid + ": " + training.size());
			// System.out.println("Training: " + training);
			db.dbPopulateTrainingSet(training, genreid, suffix);
			// System.out.println("Test: "+movies);
			db.dbPopulateTestSet(movies, genreid, suffix);
			// createTestSet(movies, genreid, suffix);
		}
	}

	// private void createTestSet(List<Integer> movies, int genreid, String
	// suffix) {
	// List<Integer> test = new ArrayList<Integer>(movies);
	// db.dbPopulateTestSet(test, genreid, suffix);
	// }

	public void createThesaurus(String name) {
		db.dbCreateThesaurus(name);
	}

	public void setStopWords(String filepath) {
		tagger.createStopList(filepath);
		System.out.println("StopList created");
	}

	// Alter this mechanism to do everything by Genre. May need new query to get
	// all films from training set with GenreID

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
			System.out.println(genreid + ": " + tagger.getWords());
			tagger.clearWords();
		}
		System.out.println("Thesaurus populated");
	}

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
			// Do something here to only add words with good frequency
			// - possibly test lower case and stop filter to try to find
			// reasonable thresholds?
			db.dbPopulateThesaurus(tagger.getWords(), genreid, name);
			System.out.println(genreid + ": " + tagger.getWords());
			tagger.clearWords();
		}
		System.out.println("Thesaurus populated");
	}

	public void trainClassifier(String name) {
		Map<Integer, String> genreList = db.dbGetGenreListMap();

		for (Integer genreid : genreList.keySet()) {
			List<String> wordsList = db.dbGetThesaurus(genreid, name);
			cls.addToDataset(genreList.get(genreid), cls.readLines(wordsList));
		}
		cls.trainClassifier();
		cls.setKnowledgeBase();
		cls.resetClassifier();
		System.out.println("Classifier Trained");
	}

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
			System.out.println(e);
		}
		System.out.println("DataClassified");

	}

	public void trainClassifier2(String name, int upper, int lower) {
		Map<Integer, String> genreList = db.dbGetGenreListMap();

		for (Integer genreid : genreList.keySet()) {
			List<String> wordsList = db.dbApplyThreshold(name, genreid, upper, lower);
			cls.addToDataset(genreList.get(genreid), cls.readLines(wordsList));
		}
		cls.trainClassifier();
		cls.setKnowledgeBase();
		cls.resetClassifier();
		System.out.println("Classifier Trained");
	}
	
	public void createClassified(String name) {
		db.dbCreateClassifiedTable(name);
		System.out.println("Classified Table Created");
	}

	public void archiveClassified(String name) {
		db.dbPopulateClassified(name, cls.getClassifiedData());
		System.out.println("DB Populated");
	}

	/*
	 * TODO: Finish these processes. Update database class.
	 */

	public void createEval(String name) {
		db.dbCreateEvalTable(name);
		System.out.println("Eval table created");
	}

	public void createEvalGenre(String name) {
		db.dbCreateEvalGenreTable(name);
		System.out.println("Eval genre table created");
	}

	/*
	 * TODO: review this
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

			if (testMap.size() > 0 && classifiedMap.size() > 0) {
				genre_count++;
				eval.runEvaluation(testMap, classifiedMap);

				db.dbAddEvalGenre(table, genreid, "Precision",
						eval.getPrecision());
				db.dbAddEvalGenre(table, genreid, "Recall", eval.getRecall());
				db.dbAddEvalGenre(table, genreid, "Fmeasure",
						eval.getFmeasure());

				precision += eval.getPrecision();
				recall += eval.getRecall();
				fmeasure += eval.getFmeasure();

				System.out.println(genreid);
				System.out.println("Precision: " + eval.getPrecision());
				System.out.println("Recall: " + eval.getRecall());
				System.out.println("Fmeasure: " + eval.getFmeasure());
			}
		}

		precision = (precision / genre_count);
		recall = (recall / genre_count);
		fmeasure = (fmeasure / genre_count);

		db.dbCreateEvalTable("Total_" + table);
		db.dbAddEval("Total_" + table, classified, "Precision", precision);
		db.dbAddEval("Total_" + table, classified, "Recall", recall);
		db.dbAddEval("Total_" + table, classified, "Fmeasure", fmeasure);

	}

	public boolean tableExists(String name) {
		return db.tableExists(name);
	}

	public void deleteContent(String name) {
		db.dbDeleteFromTable(name);
	}

	public void testEval(int tp, int fp, int fn) {
		eval.setTP(tp);
		eval.setFP(fp);
		eval.setFN(fn);

		// eval.setPrecision();
		System.out.println("Precision: " + eval.getPrecision());
		// eval.setRecall();
		System.out.println("Recall: " + eval.getRecall());
		// eval.setFmeasure();
		System.out.println("Fmeasure: " + eval.getFmeasure());
	}

}
