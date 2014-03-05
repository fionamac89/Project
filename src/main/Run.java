package main;


public class Run {

	public static void main(String[] args) {		
		ProjectSystem system = new ProjectSystem();
		//system.addGenreList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");
		/*Movie File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt"*/
		//system.addMovieList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt");
		/*Genre File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt"*/
		//system.createTrainingSet(250);
		String thesName = "Thesaurus_SSW_NewSWList";
		String className = "NFClassified_SSW_NewSWList";
		String suffix = "250";
		//system.createTrainingSet(1000, suffix);
		//system.setStopWords("./stopwords.txt");
		//system.createThesaurus(thesName);
		//system.populateThesaurus(thesName, suffix);
		//system.createClassified(className);
		//system.trainClassifier(thesName);
		//system.classifyTestData(suffix);
		//system.archiveClassified(className);
		
		system.testDbConnect();
		
		
	}

}
