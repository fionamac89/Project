package main;

import java.util.List;

public interface ISystem {

	public void addMovie();
	public void addMovieList(String filepath);
	public void addGenre();
	public void addGenreList(String filepath);
	public void createTrainingSet(int size, String suffix);
	public void createTestSet(List<Integer> movies, int id, String suffix);
	public void createThesaurus(String name);
	public void setStopWords(String filepath);
	public void populateThesaurus(String filter, String name, String suffix);
	public void trainClassifier(String name);
	public void classifyTestData(String suffix);
	public void createClassified(String name);
	public void archiveClassified(String name);
}
