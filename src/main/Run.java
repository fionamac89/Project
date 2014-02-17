package main;


public class Run {

	public static void main(String[] args) {		
		ISystem system = new ProjectSystem();
		//system.addGenreList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");
		/*Movie File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt"*/
		//system.addMovieList("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt");
		/*Genre File: "/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt"*/
		system.createTrainingSet(250);
	}

}
