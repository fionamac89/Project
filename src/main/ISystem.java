package main;

import java.util.List;

/**
 * The Interface used between the components and the main model which is controlled by
 * the ProjectSystem class.
 * 
 * @author Fiona MacIsaac
 *
 */
public interface ISystem {

	public void addMovieList(String filepath, String list, String fg);
	public void addGenreList(String filepath);
	public void createTrainingTable(String suffix);
	public void createTestTable(String suffix);
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
	public List<String> getTables();
	public void populateThesaurus2(String string, String thesName, String suffix);
	public void createEval();
	public void runEvalPerGenre(String evalName, String suffix, String className);
	public void createEvalGenre(String evalName);
	public void trainClassifier2(String thesName, int upper, int lower);

}
