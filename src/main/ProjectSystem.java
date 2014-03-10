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
		eval = new Evaluation();
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
	
	public void createTrainingSet(int size, String suffix) {
		List<Integer> genres = db.dbGetGenreList();
		List<Integer> movies = null;
		List<Integer> training = null;
		for (Integer genreid : genres) {
			int i = 0;
			movies = new ArrayList<Integer>(db.dbGetMoviesForGenreList(genreid, "FGLink_2"));
			System.out.println("Movies: "+movies);
			training = new ArrayList<Integer>();
			int listSize = movies.size();
			while ((i < size) && (i < listSize)) {
				training.add(movies.remove(0));
				i++;
			}
			System.out.println(genreid + ": " + training.size());
			System.out.println("Training: " + training);
			//db.dbPopulateTrainingSet(training, genreid, suffix);
			System.out.println("Test: "+movies);
			//db.dbPopulateTestSet(movies, genreid, suffix);
			//createTestSet(movies, genreid, suffix);
		}
	}

	private void createTestSet(List<Integer> movies, int genreid, String suffix) {
		List<Integer> test = new ArrayList<Integer>(movies);
		db.dbPopulateTestSet(test, genreid, suffix);
	}

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
			films = db.dbGetMoviesForGenreList(genreid, "TrainingSet"+suffix);
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
			films = db.dbGetMoviesForGenreList(genreid, "TrainingSet"+suffix);
			for (Integer filmid : films) {
				overview = db.dbGetOverview(filmid);
				tagger.setFilter2(overview, filter);
				tagger.applyFilter2();
			}
			//Do something here to only add words with good frequency 
			// - possibly test lower case and stop filter to try to find reasonable thresholds?
			db.dbPopulateThesaurus(tagger.getWords(), genreid, name);
			System.out.println(genreid + ": " + tagger.getWords());
			tagger.clearWords();
		}
		System.out.println("Thesaurus populated");
	}

	public void trainClassifier(String name) {
		Map<Integer, String> genreList = db.dbGetGenreListMap();
		
		for(Integer genreid : genreList.keySet()) {
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
		for(Integer e : testSet.keySet()) {
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
	
	public void runEval(String name, String test, String classified) {
		Map<Integer, Integer> testMap = db.dbGetTestSet(test);
		Map<Integer, Integer> classifiedMap = db.dbGetTable(classified);
		
		eval.runEvaluation(testMap, classifiedMap);
		db.dbAddEval(name, classified, "Precision", eval.getPrecision());
		db.dbAddEval(name, classified, "Recall", eval.getRecall());
		db.dbAddEval(name, classified, "Fmeasure", eval.getFmeasure());

	}
	
	public void runEvalPerGenre(String name, String test, String classified) {
		Map<Integer, Integer> testMap = null;
		Map<Integer, Integer> classifiedMap = null;
		List<Integer> genres = db.dbGetGenreList();
		for(int genreid : genres) {
			testMap = db.dbGetMoviesForGenreMap(genreid, "TestSet"+test);
			classifiedMap = db.dbGetMoviesForGenreMap(genreid, classified);
			
			eval.runEvaluation(testMap, classifiedMap);
			
			db.dbAddEvalGenre(name, genreid, "Precision", eval.getPrecision());
			db.dbAddEvalGenre(name, genreid, "Recall", eval.getRecall());
			db.dbAddEvalGenre(name, genreid, "Fmeasure", eval.getFmeasure());
		}
		
	}
	
	public void deleteContent(String name) {
		db.dbDeleteFromTable(name);
	}
}
