package main;


public class Run {

	public static void main(String[] args) {		
		ISystem system = new ProjectSystem();
		//String list = "FilmList_2";
		//String fg = "FGLink_2";
		//system.createFilmList(list);
		//system.createFGLink(fg);
		//system.addGenreList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");
		/*Movie File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt"*/
		//system.addMovieList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt", list, fg);
		/*Genre File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt"*/
		//system.createTrainingSet(250);
		String thesName = "Thesaurus2_SSW_1000_NewAlg";
		String className = "NFClassified2_SSW_1000_NewAlg";
		String suffix = "1000_2";
		String evalName = "Eval_None_250";
		//system.createTrainingTable(suffix);
		//system.createTestTable(suffix);
		//system.createTrainingSet(1000, suffix);
		//system.setStopWords("./stopwords.txt");
		//system.createThesaurus(thesName);
		//system.populateThesaurus2("s", thesName, suffix);
		//system.createClassified(className);
		system.trainClassifier2(thesName, 200, 5);
		system.classifyTestData(suffix);
		system.archiveClassified(className);
		
		//system.createEvalGenre(evalName);
		//system.runEvalPerGenre(evalName, suffix, className);
		
		//system.enforceThresholds("Thesaurus2_SSW_1000_NewAlg", 200, 5);

	}

}
