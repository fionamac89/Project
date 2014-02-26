package main;

import java.util.List;

public interface ISystem {

	public void addMovie();
	public void addMovieList(String filepath);
	public void addGenre();
	public void addGenreList(String filepath);
	public void createTrainingSet(int size);
	public void createTestSet(List<Integer> movies, int id);
	public void createThesaurus(String name);
	public void populateThesaurus(String name);
	public void trainClassifier(String name);
	public void classifyTestData();
	public void createClassified(String name);
	public void archiveClassified(String name);
}
