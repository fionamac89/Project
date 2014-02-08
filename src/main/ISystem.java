package main;

public interface ISystem {

	public void addMovie();
	public void addMovieList(String filepath);
	public void addGenre();
	public void addGenreList(String filepath);
	public void createTrainingSet(int size);
	public void createTestSet();
	public void createThesaurus(String name);
	public void populateThesaurus();
	public void classifyTestData();
	public void viewClassifiedData();
}
