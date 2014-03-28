package jsonparser;

import java.util.ArrayList;
import java.util.List;
/**
 * Class that is used in conjunction with the parser to allow a Movie
 * object to be created that can be broken down to it's components when
 * adding the information to the database.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Movie {
	private List<String> genres = null;
	private String title = "";
	private long id = 0;
	private String overview = "";
	
	public Movie() {
		this.genres = new ArrayList<String>();
	}

	/**
	 * Return the genres for the movie.
	 * 
	 * @return
	 */
	public List<String> getGenres() {
		return genres;
	}

	/**
	 * Add a genre to a movie.
	 * 
	 * @param genre
	 */
	public void setGenres(String genre) {
		this.genres.add(genre);
	}

	/**
	 * Return the title of the movie.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the movie.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Return the id of the movie.
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of the movie.
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Return the overview of the movie.
	 * 
	 * @return
	 */
	public String getOverview() {
		return overview;
	}

	/**
	 * Set the overview of the movie.
	 * @param overview
	 */
	public void setOverview(String overview) {
		this.overview = overview;
	}
	
}
