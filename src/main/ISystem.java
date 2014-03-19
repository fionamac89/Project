package main;


public interface ISystem {

	public void addMovie();
	public void addMovieList(String filepath, String list, String fg);
	public void addGenre();
	public void addGenreList(String filepath);
	public void createTrainingSet(int size, String suffix, String source);
	public void createThesaurus(String name);
	public void setStopWords(String filepath);
	public void populateThesaurus(String filter, String name, String suffix);
	public void trainClassifier(String name);
	public void classifyTestData(String suffix);
	public void createClassified(String name);
	public void archiveClassified(String name);
	public void deleteContent(String name);
	public boolean tableExists(String name);
	public void populateThesaurus2(String string, String thesName, String suffix);
	public void createEval(String evalName);
	public void testEval(int tp, int fp, int fn);
	public void runEvalPerGenre(String evalName, String suffix, String className);
	public void createEvalGenre(String evalName);
}
